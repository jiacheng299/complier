package newIR.Instruction;

import newIR.Value;
import newIR.ValueType;

public class StoreInstruction extends Instruction {
    public StoreInstruction(Value value1,Value value2) {
        this.value1 = value1;
        this.value2 = value2;
    }
    public void print(){
        if (value2.valueType== ValueType.i32||value2.valueType==ValueType.onearray||value2.valueType==ValueType.twoarray){
            System.out.println("store "+value1.valueType+" "+value1.name+", i32"+" * "+value2.name);
        }
        else if (value2.valueType==ValueType.pointer){
            System.out.println("store i32*"+" "+value1.name+", i32*"+" * "+value2.name);
        }
        else System.out.println("store "+value1.valueType+" "+value1.name+", "+value2.valueType+" * "+value2.name);
    }
}
