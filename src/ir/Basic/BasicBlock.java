package ir.Basic;

import ir.Instruction.BaseInstruction;
import ir.Value;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock extends Value {
    private boolean hasTerminator = false;
    private List<BaseInstruction> instructions=new ArrayList<BaseInstruction>();
    private BasicBlock outblock;//后继block
    private BasicBlock nextblock;//前驱block
    public boolean enterLoop;
    public boolean exitLoop;
    public BasicBlock() {
    }

    public BasicBlock(List<BaseInstruction> instructions, BasicBlock nextBlock, BasicBlock jumpBlock) {
        this.instructions = instructions;
        this.outblock = nextBlock;
        this.nextblock = jumpBlock;
    }
    public void addInstruction(BaseInstruction instruction) {
        instructions.add(instruction);
    }
    public void print(){
        System.out.println(";<label>:"+name);
        for (BaseInstruction instruction : instructions){
            System.out.print("    ");
            instruction.print();
        }
    }
    public void setName(String num){
        this.name=num;
    }
    public void setOutBlock(BasicBlock block) {
        outblock = block;
    }
    public void setNextBlock(BasicBlock block) {
        nextblock = block;
    }
    public BasicBlock getOutblock(){
        return outblock;
    }
    public BasicBlock getNextblock(){
        return nextblock;
    }
    public boolean hasTerminator() {
        return hasTerminator;
    }
    public void setHasTerminator(){
        hasTerminator=true;
    }
    public List<BaseInstruction> getInstructions() {return instructions;}
}
