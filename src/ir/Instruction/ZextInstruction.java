package ir.Instruction;

import ir.Type.ValueType;
import ir.Value;

public class ZextInstruction extends  BaseInstruction{
    protected ValueType ty;
    public ZextInstruction(Value result,Value value,ValueType ty){
        this.ty =ty;
        this.result = result;
        this.value1 = value;
    }
    public void print(){
        System.out.println(result.getName()+" = zext "+value1.getType()+" "+value1.getName()+" to "+ty);
    }
}
