package ir.Instruction;

import ir.Type.DataType;
import ir.Value;

import java.util.ArrayList;

public class RetInstruction extends BaseInstruction {
    private DataType type;
    public RetInstruction(Value value,DataType type){
        this.result=value;
        this.type=type;
    }
    public void print(){
        if (type == DataType.VOID){
            System.out.println("ret void");
        }
        else System.out.println("ret i32 "+result.getName());
    }
}
