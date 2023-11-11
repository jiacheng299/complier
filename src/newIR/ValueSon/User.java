package newIR.ValueSon;

import newIR.Value;
import newIR.ValueType;

public class User extends Value {
    public User(String name, ValueType valueType){
        this.name = name;
        this.valueType = valueType;
    }
}
