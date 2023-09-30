package node;
import Token.Token;

import java.util.List;

//VarDecl → BType VarDef { ',' VarDef } ';'
public class VarDeclNode {
    private BTypeNode bTypeNode;
    private VarDefNode varDefNode;
    private List<Token> commas;
    private List<VarDefNode>varDefNodes;
    private Token semi;
    public VarDeclNode(BTypeNode bTypeNode,VarDefNode varDefNode,List<Token> commas,List<VarDefNode>varDefNodes,Token semi) {
        this.bTypeNode=bTypeNode;
        this.varDefNode=varDefNode;
        this.commas=commas;
        this.varDefNodes=varDefNodes;
        this.semi=semi;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        bTypeNode.print();
        varDefNode.print();
        for (int i=0;i<commas.size();i++){
            System.out.println(commas.get(i).toString());
            varDefNodes.get(i).print();
        }
        System.out.println(semi.toString());
        System.out.println("<VarDecl>");
    }

    public BTypeNode getbTypeNode() {
        return bTypeNode;
    }

    public VarDefNode getVarDefNode() {
        return varDefNode;
    }

    public List<Token> getCommas() {
        return commas;
    }

    public List<VarDefNode> getVarDefNodes() {
        return varDefNodes;
    }

    public Token getSemi() {
        return semi;
    }
}
