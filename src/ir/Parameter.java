package ir;

import ir.Type.ValueType;

public class Parameter {
    protected ValueType dataType;

    public Parameter(ValueType dataType) {
        this.dataType = dataType;
    }

    public void print() {
        if (dataType==ValueType.i8_){
            System.out.print("i8*");
        }
        else System.out.print(dataType);
    }
}
