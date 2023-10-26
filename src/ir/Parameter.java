package ir;

import ir.Type.ValueType;

public class Parameter extends Value {

    public Parameter(ValueType dataType) {
        this.type = dataType;
    }
    public Parameter(String name,ValueType dataType){
        this.name = name;
        this.type = dataType;
    }
    public void print() {
        if (this.name != null){
            System.out.print(type+" "+name);
        }
        else{
            if (type==ValueType.i8_){
                System.out.print("i8*");
            }
            else System.out.print(type);
        }

    }
}
