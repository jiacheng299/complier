package newIR.Module;

import ir.Instruction.BaseInstruction;
import newIR.Instruction.Instruction;
import newIR.Instruction.RetInstruction;
import newIR.Value;
import newIR.ValueType;

import java.util.List;

public class BasicBlock extends Value{
    public String name;
    public List<Instruction> instructions;
    public boolean hasTerminator = false;
    public BasicBlock() {

    }
    public void addInst(Instruction instr) {
        if (!hasTerminator) instructions.add(instr);
    }

    public void setName(String id) {
        name=id;
    }
    public BasicBlock enterNextBlock(){
        BasicBlock block=new BasicBlock();
        if (!this.hasTerminator){
            instructions.add(new RetInstruction(new Value(), ValueType.VOID));
        }
        return block;
    }
    public BasicBlock enterNextBlock(BasicBlock block){
        if (!this.hasTerminator){
            instructions.add(new RetInstruction(new Value(), ValueType.VOID));
        }
        return block;
    }

    public void print() {
        System.out.println(";<label>:"+name);
        for (Instruction instruction : instructions){
            System.out.print("    ");
            instruction.print();
        }
    }
}
