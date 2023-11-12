package newIR.ValueSon;

import newIR.Value;
import newIR.ValueType;

import java.util.ArrayList;
import java.util.List;

public class Global extends Value {
    public String name;
    public boolean isConst;
    public Integer onearrayNum=null;
    public Integer twoarrayNum=null;
    public Integer num=null;
    public List<Value> arrayNums;
    public Global(String name, ValueType type,boolean isConst){
        this.name = name;
        this.isConst = isConst;
        this.valueType = type;
        this.arrayNums = new ArrayList<Value>();
    }

    public void print() {
        if (this.valueType== ValueType.i32) {
            if (this.isConst)
                System.out.println(this.name + " = dso_local constant " + this.valueType + " " + this.num);
            else
                System.out.println(this.name + " = dso_local global " + this.valueType + " " + this.num);
        }
        else if (this.arrayNum!=null){
            if (this.isConst)
                System.out.print(this.name +" = dso_local constant "+this.printArrayType()+" [");
            else
                System.out.print(this.name +" = dso_local global "+this.printArrayType()+" [");
            for (int i=0;i<this.arrayNum.size();i++){
                if (i!=this.arrayNum.size()-1) System.out.print("i32 "+this.arrayNum.get(i)+", ");
                else System.out.println("i32 "+this.arrayNum.get(i)+"]");
            }
        }
        else{
            if (this.isConst)
                System.out.println(this.name +" = dso_local constant "+this.printArrayType()+" zeroinitializer");
            else
                System.out.println(this.name +" = dso_local global "+this.printArrayType()+" zeroinitializer");
        }
    }


}
