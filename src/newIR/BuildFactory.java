package newIR;

import newIR.Instruction.*;
import newIR.Module.*;
import newIR.Module.MyModule;
import newIR.ValueSon.Const;
import newIR.ValueSon.Global;
import newIR.ValueSon.User;
import newIR.ValueSon.Var;

public class BuildFactory {
    private static final BuildFactory buildFactory=new BuildFactory();
    public static BuildFactory getBuildFactory() {return buildFactory;}
    private Integer id=1;
    public String  getId(){
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(Integer.toString(id));
        id++;
        return sb.toString();
    }
    public Function createFunction(MyModule myModule, String name, ValueType returnType, boolean isDefined){
        Function function=new Function(name,returnType,isDefined);
        myModule.functionList.add(function);
        return function;
    }
    public Global createGlobal(MyModule myModule, String name,ValueType valueType,boolean isConst){
        Global global=new Global(name,valueType,isConst);
        myModule.globalList.add(global);
        return global;
    }

    public User createVar(BasicBlock basicBlock,String name, ValueType type, boolean isconst) {
        User user=new User(getId(),type);
        user.isConst = isconst;
        AllocateInstruction allocateInstrution=new AllocateInstruction(user);
        basicBlock.addInst(allocateInstrution);
        return user;
    }

    public User createAllocateInst(BasicBlock currentBlock, ValueType valueType) {
        User user=new User(getId(),valueType);
        AllocateInstruction allocateInstrution=new AllocateInstruction(user);
        currentBlock.addInst(allocateInstrution);
        return user;
    }
    public void createStoreInst(BasicBlock currentBlock, Value value1,Value value2){
        StoreInstruction storeInstrution=new StoreInstruction(value1,value2);
        currentBlock.addInst(storeInstrution);
    }
    public User createGetElementPtr(BasicBlock currentBlock,Value value1, Value index1, Value index2){
        User user=new User(getId(),ValueType.i32);
        GetElementPtr getElementPtr=new GetElementPtr(user,value1,index1,index2);
        return user;
    }

    public Var createVar(String name, ValueType i32, boolean isConst) {
        Var var=new Var(name,i32,isConst);
        return var;
    }

    public Value createZextInst(BasicBlock currentBlock,Value value, ValueType i32) {
        User user=new User(getId(),ValueType.i32);
        ZextInstruction zextInstruction=new ZextInstruction(user, value,i32);
        currentBlock.addInst(zextInstruction);
        return user;
    }

    public Value createBinaryInst(Value newValue1, Value newValue2, OpCode opCode) {
        User user=new User(getId(),ValueType.i32);
        BinaryInstruction binaryInstruction=new BinaryInstruction(newValue1,newValue2,user,opCode);
        return user;
    }

    public User createIcmp(BasicBlock currentBlock, Value value1, Const aconst, OpCode eq) {
        User user=new User(getId(),ValueType.i1);
        IcmpInstruction icmpInstruction=new IcmpInstruction(user,value1,aconst,eq);
        currentBlock.addInst(icmpInstruction);
        return user;
    }
    public CallInstruction createCallInst(BasicBlock currentBlock,Function function){
        if (function.returnType!=ValueType.VOID){
            User user=new User(getId(),ValueType.i32);
            CallInstruction callInstruction=new CallInstruction(function,user);
            currentBlock.addInst(callInstruction);
            return callInstruction;
        }
        else{
            CallInstruction callInstruction=new CallInstruction(function);
            currentBlock.addInst(callInstruction);
            return callInstruction;
        }
    }
    public User createLoadInst(BasicBlock currentBlock,Value value){
        User user=new User(getId(),value.valueType);
        LoadInstruction loadInstruction=new LoadInstruction(user,value);
        currentBlock.addInst(loadInstruction);
        return user;
    }
}
