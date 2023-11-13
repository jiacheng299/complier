package newIR;


import newIR.Instruction.*;
import newIR.Module.*;
import newIR.Module.MyModule;
import newIR.ValueSon.Global;
import newIR.ValueSon.User;
import newIR.ValueSon.Var;

import java.util.ArrayList;
import java.util.List;

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
        Global global=new Global("@"+name,valueType,isConst);
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
        User user=new User(getId(),ValueType.pointer);
        GetElementPtr getElementPtr=new GetElementPtr(user,value1,index1,index2);
        currentBlock.addInst(getElementPtr);
        return user;
    }
    public User createGetElementPtr(BasicBlock currentBlock,Value value1, Value index1){
        User user=new User(getId(),ValueType.pointer);
        GetElementPtr getElementPtr=new GetElementPtr(user,value1,index1);
        currentBlock.addInst(getElementPtr);
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

    public Value createBinaryInst(BasicBlock currentBlock, Value newValue1, Value newValue2, OpCode opCode) {
        User user=new User(getId(),ValueType.i32);
        BinaryInstruction binaryInstruction=new BinaryInstruction(newValue1,newValue2,user,opCode);
        currentBlock.addInst(binaryInstruction);
        return user;
    }

    public User createIcmp(BasicBlock currentBlock, Value value1, Value aconst, OpCode eq) {
        User user=new User(getId(),ValueType.i1);
        IcmpInstruction icmpInstruction=new IcmpInstruction(user,value1,aconst,eq);
        currentBlock.addInst(icmpInstruction);
        return user;
    }
    public User createCallInst(BasicBlock currentBlock, Function function, List<Value> params){
        if (function.returnType!=ValueType.VOID){
            User user=new User(getId(),ValueType.i32);
            CallInstruction callInstruction=new CallInstruction(function, user, (ArrayList<Value>) params);
            currentBlock.addInst(callInstruction);
            return user;
        }
        else{
            CallInstruction callInstruction=new CallInstruction(function, (ArrayList<Value>) params);
            currentBlock.addInst(callInstruction);
            return null;
        }
    }
    public User createLoadInst(BasicBlock currentBlock,Value value){
        User user=new User(getId(),ValueType.i32);
        if (value.valueType==ValueType.pointer) user.valueType=ValueType.pointer;
        LoadInstruction loadInstruction=new LoadInstruction(user,value);
        currentBlock.addInst(loadInstruction);
        return user;
    }
    public void createRetInst(BasicBlock currentBlock,Value value,ValueType valueType){
        RetInstruction retInstruction=new RetInstruction(value,valueType);
        currentBlock.addInst(retInstruction);
        currentBlock.hasTerminator=true;
    }
    public void resetId0() {
        id=0;
    }

    public void createBranchInst(BasicBlock currentBlock, Value value, Value trueblock, Value falseblock) {
        BranchInstruction branchInst = new BranchInstruction(value,trueblock,falseblock);

        currentBlock.addInst(branchInst);
        currentBlock.hasTerminator=true;
    }

    public void createBranchInst(BasicBlock currentBlock, Value value) {
        BranchInstruction branchInst = new BranchInstruction(value);

        currentBlock.addInst(branchInst);
        currentBlock.hasTerminator=true;
    }
}
