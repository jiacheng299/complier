package ir.Basic;


import ir.Basic.BasicBlock;
import ir.Parameter;
import ir.Type.DataType;
import ir.Variable;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class Function {
    private String name;
    private List<Parameter> parameters;//参数列表
    private List<Variable> localVariables;//局部变量列表
    private List<BasicBlock> basicBlocks;//基本块列表
    private BasicBlock entryBlock;//入口block
    private BasicBlock exitBlock;//出口block
    private DataType returnType;
    public Function(String name,DataType returnType) {
        this.name = name;
        this.returnType =returnType;
        this.parameters = new ArrayList<>();
        this.localVariables = new ArrayList<>();
        this.basicBlocks = new ArrayList<>();
        this.entryBlock = null;
        this.exitBlock = null;
    }

    public List<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    public void addLocalVariable(Variable variable) {
        localVariables.add(variable);
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public void setEntryBlock(BasicBlock block) {
        entryBlock = block;
    }

    public void setExitBlock(BasicBlock block) {
        exitBlock = block;
    }

    public void print(){
        if (returnType==DataType.i32)
        System.out.println("define dso_local "+returnType+" @"+name);
    }
}
