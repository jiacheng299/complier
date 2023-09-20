package node;
import front.*;

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
        System.out.println(constToken.toString());
        bTypeNode.print();
        constDefNode.print();
        for (Token comma:commas){
            comma.print();
        }
        for(ConstDefNode constDefNode1:constDefNodes){
            constDefNode1.print();
        }
        System.out.println("ConstDeclNode");
    }
}
