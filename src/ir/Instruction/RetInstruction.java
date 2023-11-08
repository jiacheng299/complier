package ir.Instruction;

import ir.Type.ValueType;
import ir.Value;

public class RetInstruction extends BaseInstruction {
    public ValueType type;
    public RetInstruction(Value value,ValueType type){
        this.result=value;
        this.type=type;
    }
    public void print(){
        if (type == ValueType.VOID){
            System.out.println("ret void");
        }
        else System.out.println("ret i32 "+result.getName());
    }
}
