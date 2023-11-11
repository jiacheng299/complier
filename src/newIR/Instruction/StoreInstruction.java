package newIR.Instruction;

import newIR.Value;

public class StoreInstruction extends Instruction {
    public StoreInstruction(Value value1,Value value2) {
        this.value1 = value1;
        this.value2 = value2;
    }
}
