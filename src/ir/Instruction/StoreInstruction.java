package ir.Instruction;

import ir.Value;

public class StoreInstruction extends BaseInstruction{
    public StoreInstruction(Value value1, Value value2) {
        this.value1 = value1;
        this.value2 = value2;
    }
    public void print(){
        System.out.println("store "+value1.getType()+" "+value1.getName()+", "+value2.getType()+"* "+value2.getName());
    }
}
