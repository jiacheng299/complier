package ir;

import ir.Basic.BasicBlock;
import ir.Basic.Function;

import java.util.ArrayList;
import java.util.List;

public class Module {
    private List<GlobalVar> globalVars=new ArrayList<>();
    private List<Function> functions=new ArrayList<>();
    private static Module module=new Module();

    public static Module getModule() {
        return module;
    }

    public Module() {

    }
    public void print(){
        for(Function f : functions){
            f.print();
            System.out.println("{\n");
            for(BasicBlock basicBlock:f.getBasicBlocks()){
                basicBlock.print();
            }
            System.out.println("}\n");
        }
    }
    public void addFunction(Function function) {
        module.functions.add(function);
    }
    public void addGlobalVar(ir.GlobalVar globalVar) {
        module.globalVars.add(globalVar);
    }
}
