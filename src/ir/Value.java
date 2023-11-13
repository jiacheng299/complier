package ir;

import ir.Type.ValueType;

import java.util.ArrayList;
import java.util.List;

public class Value {
    protected String name=null;
    protected Integer num;
    protected ValueType type;
    public String onearrayNum=null;
    public String twoarrayNum=null;
    public boolean isConst=false;
    public boolean isGlobal=false;
    public List<String> arrayNum=new ArrayList<>();
    private List<Use> uses;
    public Value(){}
    public Value(String name, ValueType type) {
        this.name=name;
        this.type = type;
        this.uses = new ArrayList<>();
    }
    public void setType(ValueType type){
        this.type = type;
    }
    public void setName(String name){this.name=name;}
    public ValueType getType() {return type;}
    public void addUse(Use use) {
        this.uses.add(use);
    }
    public String getName() {
        return name;
    }
    public void setOnearrayNum(String num){
        this.onearrayNum=num;
    }
    public void setTwoarrayNum(String num){
        this.twoarrayNum=num;
    }
    public String printArrayType() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (twoarrayNum != null) {
            sb.append(String.valueOf((Integer.parseInt(onearrayNum) * Integer.parseInt(twoarrayNum))));
        } else {
            sb.append(onearrayNum);
        }
        sb.append(" x ");
        sb.append("i32]");
        return sb.toString();
    }
    public void setArrayNum(List<String> arrayNum){
        this.arrayNum=arrayNum;
    }

    public Integer getNum() {
        return num;
    }
    public void setNum(Integer num){
        this.num=num;
    }
}
