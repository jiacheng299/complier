package ir.Instruction;

import ir.Type.ValueType;
import ir.User;
import ir.Value;

public class LoadInstruction extends BaseInstruction{
    public LoadInstruction(User result, Value value) {
        this.result = result;
        this.value1 = value;
    }
    public void print(){
        if (result.getType()== ValueType.i32||result.getType()==ValueType.onearray||result.getType()==ValueType.twoarray) System.out.println(result.getName()+" = load i32"+", "+"i32 * "+value1.getName());
        else if (result.getType()==ValueType.i32_) System.out.println(result.getName()+" = load i32*"+", "+"i32* * "+value1.getName());
        else System.out.println(result.getName()+" = load "+result.getType()+", "+value1.getType()+"* "+value1.getName());
    }
}
