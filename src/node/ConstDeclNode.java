package node;
import Token.Token;

import java.util.List;
//ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
public class ConstDeclNode {
    private Token constToken;
    private BTypeNode bTypeNode;
    private ConstDefNode constDefNode;
    private List<Token> commas;
    private List<ConstDefNode>constDefNodes;
    private Token semiToken;
    public ConstDeclNode(Token constToken,BTypeNode bTypeNode,ConstDefNode constDefNode,List<Token> commas,List<ConstDefNode>constDefNodes,Token semiToken){
        this.constToken=constToken;
        this.bTypeNode=bTypeNode;
        this.constDefNode=constDefNode;
        this.constDefNodes=constDefNodes;
        this.commas=commas;
        this.semiToken=semiToken;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        System.out.println(constToken.toString());
        bTypeNode.print();
        constDefNode.print();
        for (int i=0;i<commas.size();i++){
            System.out.println(commas.get(i).toString());
            constDefNodes.get(i).print();
        }
        System.out.println(semiToken.toString());
        System.out.println("<ConstDecl>");
    }

    public Token getConstToken() {
        return constToken;
    }

    public BTypeNode getbTypeNode() {
        return bTypeNode;
    }

    public ConstDefNode getConstDefNode() {
        return constDefNode;
    }

    public List<Token> getCommas() {
        return commas;
    }

    public List<ConstDefNode> getConstDefNodes() {
        return constDefNodes;
    }

    public Token getSemiToken() {
        return semiToken;
    }
}
