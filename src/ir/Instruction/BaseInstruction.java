package ir.Instruction;

import ir.Value;

import java.util.ArrayList;

public class BaseInstruction {
    public Value result;
    public Value value1;
    public Value value2;

    // 定义两个“抽象”接口
    public ArrayList<Value> getUse(){
        return null;
    }
    public  ArrayList<Value> getDef(){
        return null;
    }
}
