package ir;

import ir.Type.ValueType;

import java.util.ArrayList;
import java.util.List;

public class User extends Value{
    private List<Value> oprendList;
    public User(String name, ValueType valueType){
        this.oprendList = new ArrayList<Value>();
        this.name=name;
        this.type = valueType;
    }
}
