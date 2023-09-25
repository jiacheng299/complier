package symbol;

import java.util.HashMap;
import java.util.Map;

public class SymbolTableNode {
    //每个节点都是一张符号表
    private Map<String, SymbolInfo> symbolMap;
    //指向父节点
    private SymbolTableNode parent;
    public SymbolTableNode(SymbolTableNode parent) {
        this.symbolMap = new HashMap<>();
        this.parent = parent;
    }

    public void addSymbol(String name, SymbolInfo info) {
        symbolMap.put(name, info);
    }

    public SymbolInfo getSymbol(String name) {
        SymbolInfo info = symbolMap.get(name);
        if (info == null && parent != null) {
            return parent.getSymbol(name); // 向上递归查询父符号表
        }
        return info;
    }

    public SymbolTableNode getParent() {
        return parent;
    }
}
