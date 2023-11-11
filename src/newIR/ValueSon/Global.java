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
}
