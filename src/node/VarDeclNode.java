package node;
import front.*;

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
        for (Token comma:commas){
            System.out.println(comma.toString());
        }
        for(VarDefNode varDefNode1:varDefNodes){
            varDefNode1.print();
        }
        System.out.println(semi.toString());
        System.out.println("<VarDecl>");
    }
}
