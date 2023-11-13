package newIR.ValueSon;

import newIR.Value;
import newIR.ValueType;

import java.util.ArrayList;
import java.util.List;

public class Global extends Value {
    public Global(String name, ValueType type,boolean isConst){
        this.name = name;
        this.isConst = isConst;
        this.valueType = type;
    }

    public void print() {
        if (this.valueType== ValueType.i32) {
            if (this.isConst)
                System.out.println(this.name + " = dso_local constant " + this.valueType + " " + this.constNum);
            else
                System.out.println(this.name + " = dso_local global " + this.valueType + " " + this.constNum);
        }
        else if (this.arrayNum!=null){
            if (this.isConst)
                System.out.print(this.name +" = dso_local constant "+this.printArrayType()+" [");
            else
                System.out.print(this.name +" = dso_local global "+this.printArrayType()+" [");
            for (int i=0;i<this.arrayNum.size();i++){
                if (i!=this.arrayNum.size()-1) System.out.print("i32 "+this.arrayNum.get(i).constNum+", ");
                else System.out.println("i32 "+this.arrayNum.get(i).constNum+"]");
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
