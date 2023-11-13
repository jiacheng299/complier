package newIR.ValueSon;

import newIR.Value;
import newIR.ValueTable;
import newIR.ValueType;

import java.util.ArrayList;
import java.util.List;

public class Var extends Value {
    public Var(String name, ValueType valueType, boolean isConst){
        this.name = name;
        this.valueType = valueType;
        this.isConst = isConst;
        this.arrayNum = new ArrayList<>();
    }
}
