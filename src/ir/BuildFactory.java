package ir;

import ir.Basic.BasicBlock;
import ir.Basic.Const;
import ir.Basic.GlobalVar;
import ir.Instruction.BinaryInstruction;
import ir.Instruction.Instruction;
import ir.Instruction.OpCode;
import ir.Instruction.RetInstruction;
import ir.Type.DataType;
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
    public void createRetInst(BasicBlock block, Value value, DataType type){
        RetInstruction retInst = new RetInstruction(value,type);
        block.addInstruction(retInst);
    }
    public Const createConst(String name){
        return new Const(name);
    }
    public void createBinaryInst(BasicBlock basicBlock, Value value1, Value value2, User user,OpCode op){
        BinaryInstruction binaryInstruction=new BinaryInstruction(value1,value2,user,op);
        basicBlock.addInstruction(binaryInstruction);
    }
    public GlobalVar createGlobalVar(String name,ValueType valueType ,boolean isConst){
        GlobalVar globalVar=new GlobalVar(name,valueType,isConst);
        currentModule.addGlobalVar(globalVar);
        return globalVar;
    }
}
