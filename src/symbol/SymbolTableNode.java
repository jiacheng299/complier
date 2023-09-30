package symbol;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTableNode {
    private static final SymbolTableNode globalSymbolTable=new SymbolTableNode(null);
    private static SymbolTableNode currentNode=globalSymbolTable;
    public static SymbolTableNode getCurrentNode() {
        return currentNode;
    }
    //每个节点都是一张符号表
    private LinkedHashMap<String, SymbolInfo> symbolMap;
    //指向父节点
    private SymbolTableNode parent;
    public SymbolTableNode(SymbolTableNode parent) {
        this.symbolMap = new LinkedHashMap<>();
        this.parent = parent;
    }

    public LinkedHashMap<String, SymbolInfo> getSymbolMap() {
        return symbolMap;
    }

    public void addSymbol(String name, SymbolInfo info) {
        symbolMap.put(name, info);
    }
    //在自己及父级作用域中寻找symbol
    public SymbolInfo getSymbol(String name) {
        SymbolInfo info = symbolMap.get(name);
        if (info == null && parent != null) {
            return parent.getSymbol(name); // 向上递归查询父符号表
        }
        return info;
    }
    //仅在当前作用域下寻找symbol
    public SymbolInfo getSymbolInCurrent(String name){
        SymbolInfo info =symbolMap.get(name);
        if (info==null){
            return null;
        }
        return info;
    }
    public void enterScope() {
        SymbolTableNode newScope = new SymbolTableNode(currentNode);
        currentNode = newScope;
    }
    public void exitScope(){
        if (currentNode.parent!=null){
            currentNode=currentNode.parent;
        }
    }
    public SymbolTableNode getParent() {
        return parent;
    }
}
