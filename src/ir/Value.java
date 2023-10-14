package ir;

import ir.Type.ValueType;

import java.util.ArrayList;
import java.util.List;

public class Value {
    protected String name;
    protected ValueType type;
    private List<Use> uses;
    public Value(){}
    public Value(String name, ValueType type) {
        this.name=name;
        this.type = type;
        this.uses = new ArrayList<>();
    }
    public ValueType getType() {return type;}
    public void addUse(Use use) {
        this.uses.add(use);
    }
    public String getName() {
        return name;
    }
}
