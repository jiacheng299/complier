package newIR.Module;

import newIR.Instruction.BranchInstruction;
import newIR.Instruction.Instruction;
import newIR.Instruction.RetInstruction;
import newIR.Value;
import newIR.ValueType;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock extends Value{
    public List<Instruction> instructions=new ArrayList<>();
    public boolean hasTerminator = false;
    public BasicBlock outblock;//后继block
    public BasicBlock nextblock;//前驱block
    public boolean enterLoop=false;
    public boolean exitLoop=false;

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
            hasTerminator=true;
        }
        return block;
    }
    public BasicBlock enterNextBlock(BasicBlock block){
        if (!this.hasTerminator){
            instructions.add(new BranchInstruction(block));
            hasTerminator=true;
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
