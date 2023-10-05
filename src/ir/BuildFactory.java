package ir;

public class BuildFactory {
    private static final BuildFactory buildFactory=new BuildFactory();
    public static BuildFactory getBuildFactory(){
        return buildFactory;
    }
    public void createRetInst(Integer id,BasicBlock block){
        Instruction instruction=new Instruction("return",id,block);
        block.addInstruction(instruction);
    }
    public void createRetInst(Integer id,BasicBlock block,Value tmp){
        Instruction instruction=new Instruction("return",id,tmp,block);
        block.addInstruction(instruction);
    }
    public void createInst(OpCode opCode,Integer id,Value left,Value right,BasicBlock block){
        Instruction instruction=new Instruction("common",opCode,id,left,right,block);
        block.addInstruction(instruction);
    }
}
