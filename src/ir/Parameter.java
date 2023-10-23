package ir;

import ir.Type.DataType;

public class Parameter {
    protected DataType dataType;

    public Parameter(DataType dataType) {
        this.dataType = dataType;
    }

    public void print() {
        if (dataType==DataType.i8_){
            System.out.print("i8*");
        }
        else System.out.print(dataType);
    }
}
