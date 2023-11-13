package newIR.Instruction;

import newIR.Value;
import newIR.ValueType;

public class StoreInstruction extends Instruction {
    public StoreInstruction(Value value1,Value value2) {
        this.value1 = value1;
        this.value2 = value2;
    }
    public void print(){
        if (value1.valueType==ValueType.pointer){
            System.out.println("store i32*"+" "+value1.name+", i32*"+" * "+value2.name);
        }
        else System.out.println("store i32"+" "+value1.name+", "+"i32 "+" * "+value2.name);
    }
}
