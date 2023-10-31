package ir.Instruction;

import ir.Type.ValueType;
import ir.User;
import ir.Value;

public class AllocateInstruction extends BaseInstruction{
    public AllocateInstruction(Value value,Value param) {
        this.result = value;
        this.value1 = param;
    }
    public void print(){
        if (value1.getType()== ValueType.i32) System.out.println(result.getName()+" = alloca "+value1.getType());
        else if(value1.getType()==ValueType.i32_) System.out.println(result.getName()+" = alloca i32*");
        else System.out.println(result.getName()+" = alloca "+value1.printArrayType());
    }
}
