package newIR.Instruction;

import newIR.Value;
import newIR.ValueType;

public class AllocateInstruction extends Instruction {
    public AllocateInstruction(Value user){
        this.result=user;
    }

    @Override
    public void print() {
        if (value1.valueType == ValueType.i32) System.out.println(result.name+" = alloca "+value1.valueType);
        else if(value1.valueType==ValueType.pointer) System.out.println(result.name+" = alloca i32*");
        else System.out.println(result.name+" = alloca "+value1.printArrayType());
    }
}
