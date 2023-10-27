package ir.Instruction;

import ir.User;
import ir.Value;

public class IcmpInstruction extends BaseInstruction{
    protected OpCode op;
    public IcmpInstruction(User result, Value value1, Value value2, OpCode op) {
        this.result = result;
        this.value1 = value1;
        this.value2 = value2;
        this.op = op;
    }
    public void print(){
        System.out.println(result.getName()+" = icmp "+op.toString()+value1.getType()+" "+value1.getName()+", "+value2.getName());
    }
}
