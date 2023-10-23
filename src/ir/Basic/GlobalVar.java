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
        if (this.isGlobal==true){
            System.out.println("@"+this.name+" = dso_local global "+this.type+" "+this.num);
        }
    }
}
