package ir.Instruction;

import ir.User;
import ir.Value;

public class AllocateInstruction extends BaseInstruction{
    public AllocateInstruction(Value value,Value param) {
        this.result = value;
        this.value1 = param;
    }
    public void print(){
        System.out.println(result.getName()+" = alloca "+value1.getType());
    }
}
