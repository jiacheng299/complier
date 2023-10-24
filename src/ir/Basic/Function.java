package ir.Basic;


import ir.Parameter;
import ir.Type.ValueType;
import ir.Value;
import ir.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Function extends Value{
    private String name;
    private static HashMap<String,Function> functions = new HashMap<>();
    private List<Parameter> parameters;//参数列表
    private List<Variable> localVariables;//局部变量列表
    private List<BasicBlock> basicBlocks;//基本块列表
    private BasicBlock entryBlock;//入口block
    private BasicBlock exitBlock;//出口block
    private ValueType returnType;
    private Boolean isDefined=false;
    public Function(String name,ValueType returnType) {
        this.name = name;
        this.returnType =returnType;
        this.parameters = new ArrayList<>();
        this.localVariables = new ArrayList<>();
        this.basicBlocks = new ArrayList<>();
        this.entryBlock = null;
        this.exitBlock = null;
    }
    public void addFunction(String name,Function function) {Function.functions.put(name,function);}
    public void setDefined(){
        this.isDefined=true;
    }
    public static HashMap<String,Function> getFunctions() {
        return functions;
    }
    public ValueType getType(){
        return this.returnType;
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
        if (this.isDefined==false){
            if (returnType==ValueType.i32)
                System.out.print("declare "+returnType+" @"+name+"(");
            else if(returnType==ValueType.VOID)
                System.out.print("declare "+returnType+" @"+name+"(");
            for(int i=0;i<parameters.size();i++){
                parameters.get(i).print();
                if (i!= parameters.size()-1){
                    System.out.print(",");
                }
            }
            //加入一系列参数
            System.out.println(")");

        }
        else{
            if (returnType==ValueType.i32)
                System.out.print("define dso_local "+returnType+" @"+name+"(");
            else if(returnType==ValueType.VOID)
                System.out.print("define dso_local "+returnType+" @"+name+"(");
            for(int i=0;i<parameters.size();i++){
                parameters.get(i).print();
                if (i!= parameters.size()-1){
                    System.out.print(",");
                }
            }
            //加入一系列参数
            System.out.println(")");
            System.out.println("{");
            for(BasicBlock basicBlock:this.getBasicBlocks()){
                basicBlock.print();
            }
            System.out.println("}");
        }


    }

    public String getName() {
        return name;
    }
}
