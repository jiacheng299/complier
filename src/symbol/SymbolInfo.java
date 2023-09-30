package symbol;

import java.util.ArrayList;
import java.util.List;

public class SymbolInfo {
    private String name;
    private SymbolType type;
    private String address;
    private SymbolTableNode node;
    private Boolean isConst=false;

    public Boolean getConst() {
        return isConst;
    }

    private ReturnType returnType;
    private List<SymbolInfo> funcParams=new ArrayList<>();

    public ReturnType getReturnType() {
        return returnType;
    }

    public SymbolType getType() {
        return type;
    }

    public List<SymbolInfo> getFuncParams() {
        return funcParams;
    }

    //常量（普通变量，一维数组，二维数组）
    //变量(普通变量，一维数组，二维数组)
    //函数名
    //函数参数
    public SymbolInfo(String name, SymbolType type, SymbolTableNode node, Boolean isConst) {
        this.name = name;
        this.type = type;
        this.node = node;
        this.isConst = isConst;
    }

    public SymbolInfo(String name, SymbolType type, SymbolTableNode node, ReturnType returnType) {
        this.name = name;
        this.type = type;
        this.node = node;
        this.returnType = returnType;
    }

    public void addFuncParam(SymbolInfo symbolInfo){
        funcParams.add(symbolInfo);
    }
    public enum ReturnType{
        VOID,INT;
    }
    public enum SymbolType{
        interger,oneArray,twoArray,func,VOID;
    }
}
