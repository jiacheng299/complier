package newIR.Module;

import newIR.Instruction.Instruction;

import java.util.List;

public class BasicBlock {
    public List<Instruction> instructions;
    public BasicBlock() {

    }
    public void addInst(Instruction instr) {
        instructions.add(instr);
    }
}
