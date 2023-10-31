package ir.Instruction;

import ir.Type.ValueType;
import ir.Value;

public class StoreInstruction extends BaseInstruction{
    public StoreInstruction(Value value1, Value value2) {
        this.value1 = value1;
        this.value2 = value2;
    }
    public void print(){
        if (value2.getType()== ValueType.i32||value2.getType()==ValueType.onearray||value2.getType()==ValueType.twoarray){
            System.out.println("store "+value1.getType()+" "+value1.getName()+", i32"+" * "+value2.getName());
        }
        else if (value2.getType()==ValueType.i32_){
            System.out.println("store i32*"+" "+value1.getName()+", i32*"+" * "+value2.getName());
        }
        else System.out.println("store "+value1.getType()+" "+value1.getName()+", "+value2.getType()+" * "+value2.getName());
    }
}
