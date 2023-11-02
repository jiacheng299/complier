package ir.Basic;

import ir.Type.ValueType;
import ir.Value;

public class MyParameter extends Value {

    public MyParameter(ValueType dataType) {
        this.type = dataType;
    }
    public MyParameter(String name, ValueType dataType){
        this.name = name;
        this.type = dataType;
    }
    public void print() {
        if (this.name != null){
            if (type==ValueType.i8_){
                System.out.print("i8*");
            } else if (type==ValueType.i32_) {
                System.out.print("i32*");
            } else System.out.print(type);
            System.out.print(" "+name);
        }
        else{
            if (type==ValueType.i8_){
                System.out.print("i8*");
            } else if (type==ValueType.i32_) {
                System.out.print("i32*");
            } else System.out.print(type);
        }

    }
}
