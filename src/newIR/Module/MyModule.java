package newIR.Module;

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
}
