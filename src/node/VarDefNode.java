package node;

import Token.Token;

import java.util.List;

//VarDef → Ident { '[' ConstExp ']' }| Ident { '[' ConstExp ']' } '=' InitVal
public class VarDefNode {
    private Token ident;
    private List<Token> Lbraces;
    private List<ConstExpNode>constExpNodes;
    private List<Token>Rbraces;
    private Token assign;
    private InitValNode initValNode;
    public VarDefNode(Token ident,List<Token> Lbraces,List<ConstExpNode>constExpNodes,List<Token>Rbraces,Token assign,InitValNode initValNode) {
        this.ident=ident;
        this.Lbraces=Lbraces;
        this.constExpNodes=constExpNodes;
        this.Rbraces=Rbraces;
        this.assign=assign;
        this.initValNode=initValNode;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        System.out.println(ident.toString());
        for (int i=0;i<Lbraces.size();i++){
            System.out.println(Lbraces.get(i).toString());
            constExpNodes.get(i).print();
            System.out.println(Rbraces.get(i).toString());
        }
        if(assign!=null){
            System.out.println(assign.toString());
            initValNode.print();
        }
        System.out.println("<VarDef>");
    }

    public Token getIdent() {
        return ident;
    }

    public List<Token> getLbraces() {
        return Lbraces;
    }

    public List<ConstExpNode> getConstExpNodes() {
        return constExpNodes;
    }

    public List<Token> getRbraces() {
        return Rbraces;
    }

    public Token getAssign() {
        return assign;
    }

    public InitValNode getInitValNode() {
        return initValNode;
    }
}
