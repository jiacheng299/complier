package newIR.Instruction;


import newIR.Value;
import newIR.ValueType;

public class RetInstruction extends Instruction {
    public RetInstruction(Value value, ValueType type){
        this.result=value;
        this.valueType=type;
    }
    public void print(){
        if (valueType == ValueType.VOID){
            System.out.println("ret void");
        }
        else System.out.println("ret i32 "+result.name);
    }
}
