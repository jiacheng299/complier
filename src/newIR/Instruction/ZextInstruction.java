package newIR.Instruction;

import newIR.Value;
import newIR.ValueType;

public class ZextInstruction extends Instruction{
    protected ValueType ty;
    public ZextInstruction(Value result, Value value, ValueType ty){
        this.ty =ty;
        this.result = result;
        this.value1 = value;
    }
    public void print(){
        System.out.println(result.name+" = zext "+value1.valueType+" "+value1.name+" to "+ty);
    }
}
