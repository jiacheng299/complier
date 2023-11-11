package newIR.Instruction;

import newIR.Value;
import newIR.ValueSon.User;

public class IcmpInstruction extends Instruction{
    public OpCode op;
    public IcmpInstruction(User result, Value value1, Value value2, OpCode op) {
        this.result = result;
        this.value1 = value1;
        this.value2 = value2;
        this.op = op;
    }
    public void print(){
        System.out.println(result.name+" = icmp "+op.toString()+" "+value1.valueType+" "+value1.name+", "+value2.name);
    }
}
