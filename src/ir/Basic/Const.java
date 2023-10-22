package ir.Basic;

import ir.Type.DataType;
import ir.Type.ValueType;
import ir.Value;

public class Const extends Value {

    public Const(String name){
        this.type= ValueType.i32;
        this.name= name;
    }
    public int getValue(){
        return Integer.parseInt(name);
    }
}
