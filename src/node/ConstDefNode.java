package node;
import front.*;

import java.util.List;

//ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
public class ConstDefNode {
    private Token ident;
    private List<Token>Lbracks;
    private List<ConstExpNode>constExpNodes;
    private List<Token>Rbracks;
    private Token assign;
    private ConstInitValNode constInitValNode;
    public ConstDefNode( Token ident,List<Token>Lbracks,List<ConstExpNode>constExpNodes, List<Token>Rbracks,Token assign,ConstInitValNode constInitValNode){
        this.ident=ident;
        this.Lbracks=Lbracks;
        this.constExpNodes=constExpNodes;
        this.Rbracks=Rbracks;
        this.assign=assign;
        this.constInitValNode=constInitValNode;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        System.out.println(ident.toString());
        for (Token lbrack:Lbracks){
            System.out.println(lbrack.toString());
        }
        for(ConstExpNode constExpNode:constExpNodes){
            constExpNode.print();
        }
        for (Token rbrack:Rbracks){
            System.out.println(rbrack.toString());
        }
        System.out.println(assign.toString());
        constInitValNode.print();
        System.out.println("<ConstDef>");
    }
}
