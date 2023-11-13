package newIR.Instruction;

import newIR.Value;
import newIR.ValueType;

public class AllocateInstruction extends Instruction {
    public AllocateInstruction(Value result){
        this.result=result;
    }

    @Override
    public void print() {
        if (result.valueType == ValueType.i32) System.out.println(result.name+" = alloca "+result.valueType);
        else if(result.valueType==ValueType.pointer) System.out.println(result.name+" = alloca i32*");
        else System.out.println(result.name+" = alloca "+result.printArrayType());
    }
}
