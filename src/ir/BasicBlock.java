package ir;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    private List<Instruction> instructions=new ArrayList<Instruction>();
    private BasicBlock nextBlock;//后继block
    private BasicBlock jumpBlock;//前驱block
    public BasicBlock() {
    }
    public BasicBlock(List<Instruction> instructions, BasicBlock nextBlock, BasicBlock jumpBlock) {
        this.instructions = instructions;
        this.nextBlock = nextBlock;
        this.jumpBlock = jumpBlock;
    }
    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }
    public void print(){
        for (Instruction instruction : instructions){
            instruction.print();
        }
    }
    public void setNextBlock(BasicBlock block) {
        nextBlock = block;
    }

    public void setJumpBlock(BasicBlock block) {
        jumpBlock = block;
    }
}
