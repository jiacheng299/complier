package newIR.Module;

import ir.Basic.GlobalVar;
import newIR.ValueSon.Global;

import java.util.ArrayList;
import java.util.List;

public class MyModule {
    public List<Function> functionList;
    public List<Global> globalList;
    public MyModule(){
        this.functionList = new ArrayList<Function>();
        this.globalList=new ArrayList<>();
    }
    public void print(){
        for (Global globalVar:globalList){
            globalVar.print();
        }
        for(Function f : functionList){
            f.print();

        }
    }
}
