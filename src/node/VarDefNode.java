package node;

import front.Token;

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
        System.out.println(ident.toString());
        for(Token lbrace:Lbraces){
            System.out.println(lbrace.toString());
        }
        for (ConstExpNode constExpNode:constExpNodes){
            constExpNode.print();
        }
        for(Token rbrace:Rbraces){
            System.out.println(rbrace.toString());
        }
        if(assign!=null){
            System.out.println(assign.toString());
            initValNode.print();
        }
        System.out.println("VarDefNode");
    }
}
