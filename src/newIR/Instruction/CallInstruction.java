package newIR.Instruction;

import newIR.Module.Function;
import newIR.Value;

import java.util.ArrayList;

public class CallInstruction extends Instruction{
    public Function function;
    public ArrayList<Value> funcRParams;
    public CallInstruction(Function function, Value res) {
        this.result = res;
        this.function = function;
        funcRParams = new ArrayList<>();
    }
    public CallInstruction(Function function){
        this.function = function;
        funcRParams = new ArrayList<>();
    }
    public void addParam(Value value) {
        funcRParams.add(value);
    }
}
