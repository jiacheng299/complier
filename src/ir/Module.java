package ir;

import ir.Basic.BasicBlock;
import ir.Basic.Function;
import ir.Basic.GlobalVar;

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
        for (GlobalVar globalVar:globalVars){
            globalVar.print();
        }
        for(Function f : functions){
            f.print();

        }
    }
    public List<GlobalVar> getGlobalVars(){
        return globalVars;
    }
    public void addFunction(Function function) {
        module.functions.add(function);
    }
    public void addGlobalVar(GlobalVar globalVar) {
        module.globalVars.add(globalVar);
    }
}
