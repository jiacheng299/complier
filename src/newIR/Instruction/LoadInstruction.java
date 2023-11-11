package newIR.Instruction;

import newIR.Value;
import newIR.ValueSon.User;
import newIR.ValueType;

public class LoadInstruction extends Instruction{
    public LoadInstruction(User result, Value value) {
        this.result = result;
        this.value1 = value;
    }
    public void print(){
        if (result.valueType== ValueType.i32||result.valueType==ValueType.onearray||result.valueType==ValueType.twoarray) System.out.println(result.name+" = load i32"+", "+"i32 * "+value1.name);
        else if (result.valueType==ValueType.pointer) System.out.println(result.name+" = load i32*"+", "+"i32* * "+value1.name);
        else System.out.println(result.name+" = load "+result.valueType+", "+value1.valueType+"* "+value1.name);
    }
}
