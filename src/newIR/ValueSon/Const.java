package newIR.ValueSon;

import newIR.Value;

public class Const extends Value {
    public String num;
    public Const(String num){
        this.num = num;
        this.name=num;
        this.isConst=true;
        this.constNum=Integer.parseInt(num);
    }
}
