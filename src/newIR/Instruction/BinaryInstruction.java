package newIR.Instruction;

import newIR.Value;
import newIR.ValueSon.User;

public class BinaryInstruction extends Instruction{
    public OpCode opCode;
    public BinaryInstruction(Value value1, Value value2, User result, OpCode opCode) {
        this.value1 = value1;
        this.value2 = value2;
        this.result = result;
        this.opCode=opCode;
    }
    public void print(){
        System.out.println(this.result.name+" = "+this.opCode+" "+value1.valueType+" "+value1.name+", "+value2.name);
    }
}
