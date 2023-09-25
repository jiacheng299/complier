package symbol;

public class SymbolTable {
    private SymbolTableNode globalSymbolTable;
    private SymbolTableNode currentNode;

    public SymbolTable() {
        globalSymbolTable = new SymbolTableNode(null);
        currentNode = globalSymbolTable;
    }
    public void enterScope() {
        SymbolTableNode newScope = new SymbolTableNode(currentNode);
        currentNode = newScope;
    }

    public void exitScope() {
        if (currentNode.getParent() != null) {
            currentNode = currentNode.getParent();
        }
    }
    public void addSymbol(String name, SymbolInfo info) {
        globalSymbolTable.addSymbol(name, info);
    }

    public SymbolInfo getSymbol(String name) {
        return globalSymbolTable.getSymbol(name);
    }
}
