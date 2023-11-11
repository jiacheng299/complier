package newIR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ValueTable {
    //这个表要能根据名字找到对应的Value，先建立hashmap
    HashMap<String, Value>valueHashMap;
    //这个表要能找到父亲，方便回溯
    ValueTable father;
    //这个表要能找到儿子，方便新建
    List<ValueTable> sons=new ArrayList<>();
    public ValueTable(){
        this.valueHashMap=new HashMap<>();
    }
    public ValueTable(ValueTable father){
        this.valueHashMap=new HashMap<>();
        this.father=father;
    }
    public void addValue(String name, Value value){
        valueHashMap.put(name,value);
    }
    public Value searchValue(String name){
        if (this.valueHashMap.containsKey(name)){
            return this.valueHashMap.get(name);
        }
        else{
            if (this.father==null){return null;}
            else return this.father.searchValue(name);
        }
    }
    public ValueTable enterNextTbale(){
        //每次进入一个新的block相当于要换一个作用域
        ValueTable valueTable=new ValueTable();
        this.sons.add(valueTable);
        valueTable.father=this;
        return valueTable;
    }
}
