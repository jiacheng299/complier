package newIR.Instruction;


import newIR.Value;

public class BranchInstruction extends Instruction{
    public Value cond;


    public BranchInstruction(Value cond, Value labelTrue, Value labelFalse){
        this.cond = cond;
        this.value1=labelTrue;
        this.value2=labelFalse;
    }
    public BranchInstruction(Value dest){
        this.value1=dest;
    }
    public void print(){
        if (cond != null) {
            System.out.println("br i1  " + cond.name+", label "+value1.name + ", label "+value2.name);
        }
        else{
            System.out.println("br label "+value1.name);
        }
    }
}
