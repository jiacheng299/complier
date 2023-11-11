package newIR.Instruction;

import newIR.Value;

public class AllocateInstruction extends Instruction {
    public AllocateInstruction(Value user){
        this.result=user;
    }
}
