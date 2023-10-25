package ir.Instruction;

import ir.Basic.Function;
import ir.Value;

import java.util.ArrayList;

public class CallInstruction extends BaseInstruction{
    protected Function function;
    public ArrayList<Value> funcRParams;
    public CallInstruction(Function function, Value res) {
        this.result = res;
        this.function = function;
        funcRParams = new ArrayList<>();
    }
    public void addParam(Value value) {
        funcRParams.add(value);
    }
    public void print(){
        System.out.print(result.getName()+" = call "+function.getType()+" @"+function.getName()+"(");
        for (int i=0;i<funcRParams.size();i++){
            if(i!=0){
                System.out.print(",");
            }
            System.out.print(funcRParams.get(i).getType()+" "+funcRParams.get(i).getName());
        }
        System.out.println(")");
    }
}
