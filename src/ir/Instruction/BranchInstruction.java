package ir.Instruction;

import ir.Value;

public class BranchInstruction extends BaseInstruction{
    public Value cond;


    public  BranchInstruction(Value cond,Value labelTrue,Value labelFalse){
        this.cond = cond;
        this.value1=labelTrue;
        this.value2=labelFalse;
    }
    public BranchInstruction(Value dest){
        this.value1=dest;
    }
    public void print(){
        if (cond != null) {
            System.out.println("br i1  " + cond.getName()+", label "+value1.getName() + ", label "+value2.getName());
        }
        else{
            System.out.println("br label "+value1.getName());
        }
    }
}
