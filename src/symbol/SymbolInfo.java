package symbol;

public class SymbolInfo {
    private String name;
    private SymbolType type;
    private String address;
    private SymbolTableNode node;
    private Boolean isConst;
    enum SymbolType{
        interger;
    }
}
