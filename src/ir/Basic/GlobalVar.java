package ir.Basic;

import ir.Type.ValueType;
import ir.Value;
import ir.ValueTable;

import java.util.ArrayList;
import java.util.List;

public class GlobalVar extends Value {
    //List<Object> init;

    public GlobalVar(String name, ValueType valueType, boolean isConst){
        this.name = name;
        this.type=valueType;
        this.isConst = isConst;
        this.isGlobal = true;
    }
    //设置num
    public void setNum(Integer num){
        this.num=num;
    }
    public Integer getNum(){return this.num;}
    public void print(){
        if (this.type==ValueType.i32) {
            if (this.isConst)
                System.out.println(this.name + " = dso_local constant " + this.type + " " + this.num);
            else
                System.out.println(this.name + " = dso_local global " + this.type + " " + this.num);
        }
        else {
            if (this.isConst)
                System.out.print(this.name +" = dso_local constant "+this.printArrayType()+" [");
            else
                System.out.print(this.name +" = dso_local global "+this.printArrayType()+" [");
            for (int i=0;i<this.arrayNum.size();i++){
                if (i!=this.arrayNum.size()-1) System.out.print("i32 "+this.arrayNum.get(i)+", ");
                else System.out.println("i32 "+this.arrayNum.get(i)+"]");
            }
        }
    }
}
