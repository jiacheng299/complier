package ir.Instruction;

import ir.User;
import ir.Value;

public class LoadInstruction extends BaseInstruction{
    public LoadInstruction(User result, Value value) {
        this.result = result;
        this.value1 = value;
    }
    public void print(){
        System.out.println(result.getName()+" = load "+result.getType()+", "+value1.getType()+"* "+value1.getName());
    }
}
