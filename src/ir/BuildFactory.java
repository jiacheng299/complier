package ir;

import ir.Basic.*;
import ir.Instruction.*;
import ir.Type.ValueType;

public class BuildFactory {
    private Module currentModule=Module.getModule();
    private Integer id=1;
    public String  getId(){
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(Integer.toString(id));
        id++;
        return sb.toString();
    }
    private static final BuildFactory buildFactory=new BuildFactory();
    public static BuildFactory getBuildFactory(){
        return buildFactory;
    }
    public void createRetInst(BasicBlock block, Value value, ValueType type){
        RetInstruction retInst = new RetInstruction(value,type);
        if (!block.hasTerminator())block.addInstruction(retInst);
        block.setHasTerminator();
    }
    public Const createConst(String name){
        return new Const(name);
    }
    public void createBinaryInst(BasicBlock basicBlock, Value value1, Value value2, User user,OpCode op){
        BinaryInstruction binaryInstruction=new BinaryInstruction(value1,value2,user,op);
        if (!basicBlock.hasTerminator())basicBlock.addInstruction(binaryInstruction);
    }
    public GlobalVar createGlobalVar(String name,ValueType valueType ,boolean isConst){
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(name);
        GlobalVar globalVar=new GlobalVar(sb.toString(),valueType,isConst);
        currentModule.addGlobalVar(globalVar);
        return globalVar;
    }
    public void createAllocateInst(BasicBlock basicBlock, Value value,Value param){
        AllocateInstruction allocateInstruction = new AllocateInstruction(value,param);
        if (!basicBlock.hasTerminator())basicBlock.addInstruction(allocateInstruction);
    }
    public void createStoreInst(BasicBlock basicBlock, Value value1,Value value2){
        StoreInstruction storeInstruction = new StoreInstruction(value1, value2);
        if (!basicBlock.hasTerminator())basicBlock.addInstruction(storeInstruction);
    }
    public void createLoadInst(BasicBlock basicBlock, User value1,Value value2){
        LoadInstruction loadInstruction = new LoadInstruction(value1, value2);
        if (!basicBlock.hasTerminator())basicBlock.addInstruction(loadInstruction);
    }
    public CallInstruction createCallInst(BasicBlock basicBlock, Function function, Value value){
        CallInstruction callInstruction = new CallInstruction(function,value);
        if (!basicBlock.hasTerminator()) basicBlock.addInstruction(callInstruction);
        return callInstruction;
    }
    public CallInstruction createCallInst(BasicBlock basicBlock, Function function){
        CallInstruction callInstruction = new CallInstruction(function);
        if (!basicBlock.hasTerminator())basicBlock.addInstruction(callInstruction);
        return callInstruction;
    }
    public void resetId0(){
        this.id=0;
    }
    public void resetId1(){this.id=1;}
    public void createBranchInst(BasicBlock basicBlock,Value cond,Value trueblock,Value falseblock){
        BranchInstruction branchInst = new BranchInstruction(cond,trueblock,falseblock);

        if (!basicBlock.hasTerminator())basicBlock.addInstruction(branchInst);
        basicBlock.setHasTerminator();
    }
    public void createBranchInst(BasicBlock basicBlock,Value dest){
        BranchInstruction branchInst = new BranchInstruction(dest);
        if (!basicBlock.hasTerminator())basicBlock.addInstruction(branchInst);
        basicBlock.setHasTerminator();
    }
    public void createIcmpInst(BasicBlock currentBasicBlock, User user, Value value1, Value value2,OpCode opcode    ) {
        IcmpInstruction icmpInstruction=new IcmpInstruction(user,value1,value2,opcode);
        if (!currentBasicBlock.hasTerminator())currentBasicBlock.addInstruction(icmpInstruction);
    }
    public void createZextInst(BasicBlock currentBasicBlock,Value result,Value value,ValueType ty){
        ZextInstruction zextInstruction=new ZextInstruction(result,value,ty);
        if(!currentBasicBlock.hasTerminator())currentBasicBlock.addInstruction(zextInstruction);
    }

    public Value createGetElementPtr(BasicBlock currentBasicBlock,Value value1,Value value2,Value value3){
        User user=new User(buildFactory.getId(),ValueType.i32);
        GetElementPtr getElementPtrInstruction=new GetElementPtr(user,value1,value2,value3);
        if (!currentBasicBlock.hasTerminator())currentBasicBlock.addInstruction(getElementPtrInstruction);
        return user;
    }
    public Value createGetElementPtr(BasicBlock currentBasicBlock,Value value1,Value value2){
        User user=new User(buildFactory.getId(),ValueType.i32);
        GetElementPtr getElementPtrInstruction=new GetElementPtr(user,value1,value2);
        if (!currentBasicBlock.hasTerminator())currentBasicBlock.addInstruction(getElementPtrInstruction);
        return user;
    }
}
