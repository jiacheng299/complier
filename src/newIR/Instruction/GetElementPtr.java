package newIR.Instruction;

import newIR.Value;

public class GetElementPtr extends Instruction{
    public Value index1=null;
    public Value index2=null;
    public GetElementPtr(Value result,Value value1,Value index1,Value index2){
        this.result = result;
        this.value1=value1;
        this.index1 = index1;
        this.index2 = index2;
    }
    public GetElementPtr(Value result,Value value1,Value index1){
        this.result = result;
        this.value1 = value1;
        this.index1 = index1;
    }
}
