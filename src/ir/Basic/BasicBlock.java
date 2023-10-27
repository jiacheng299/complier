package ir.Basic;

import ir.Instruction.BaseInstruction;
import ir.Instruction.Instruction;
import ir.Value;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock extends Value {
    private List<BaseInstruction> instructions=new ArrayList<BaseInstruction>();
    private BasicBlock nextBlock;//后继block
    private BasicBlock jumpBlock;//前驱block
    public BasicBlock() {
    }

    public BasicBlock(List<BaseInstruction> instructions, BasicBlock nextBlock, BasicBlock jumpBlock) {
        this.instructions = instructions;
        this.nextBlock = nextBlock;
        this.jumpBlock = jumpBlock;
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
    public void setNextBlock(BasicBlock block) {
        nextBlock = block;
    }

    public void setJumpBlock(BasicBlock block) {
        jumpBlock = block;
    }
}
