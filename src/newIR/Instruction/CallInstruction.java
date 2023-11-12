package newIR.Instruction;


import newIR.Module.Function;
import newIR.Value;
import newIR.ValueType;

import java.util.ArrayList;

public class CallInstruction extends Instruction{
    public Function function;
    public ArrayList<Value> funcRParams;
    public CallInstruction(Function function, Value res,ArrayList<Value> params) {
        this.result = res;
        this.function = function;
        funcRParams = params;
    }
    public CallInstruction(Function function,ArrayList<Value> params){
        this.function = function;
        funcRParams = params;
    }
    public void print(){
        if (this.result!=null) System.out.print(result.name+" = call "+function.returnType.toString().toLowerCase()+" @"+function.name+"(");
        else System.out.print("call "+function.returnType.toString().toLowerCase()+" @"+function.name+"(");
        for (int i=0;i<funcRParams.size();i++){
            if(i!=0){
                System.out.print(",");
            }
            if (funcRParams.get(i).valueType== ValueType.pointer) System.out.print("i32* "+funcRParams.get(i).name);
            else  System.out.print(funcRParams.get(i).valueType+" "+funcRParams.get(i).name);
        }
        System.out.println(")");
    }
}
