package newIR.Module;

import newIR.Value;
import newIR.ValueType;

import java.util.ArrayList;
import java.util.List;

public class Function {
    public String name;
    public ValueType returnType;
    public boolean isDefined=false;
    public List<Value> params;
    public Function(String name, ValueType returnType, boolean isDefined){
        this.name=name;
        this.returnType=returnType;
        this.isDefined=isDefined;
        this.params=new ArrayList<Value>();
    }
}
