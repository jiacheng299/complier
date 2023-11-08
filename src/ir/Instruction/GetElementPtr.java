package ir.Instruction;

import ir.Value;

public class GetElementPtr extends BaseInstruction{
    public Value bound1;
    public Value bound2=null;
    public GetElementPtr(Value result, Value value1, Value bound1, Value bound2) {
        this.result = result;
        this.value1 = value1;
        this.bound1 = bound1;
        this.bound2 = bound2;
    }
    public GetElementPtr(Value result, Value value1, Value bound1) {
        this.result = result;
        this.value1 = value1;
        this.bound1 = bound1;
    }
    public void print(){
        if (bound2==null){
            System.out.println(result.getName()+" = getelementptr i32,"+" "+"i32"+"* "+value1.getName()+", "+bound1.getType()+" "+bound1.getName());
        }
        else{
            System.out.println(result.getName()+" = getelementptr "+value1.printArrayType()+", "+value1.printArrayType()+"* "+value1.getName()+", "+bound1.getType()+" "+bound1.getName()+", "+bound2.getType()+" "+bound2.getName());
        }
    }
}
