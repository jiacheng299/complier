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
        for (int i=0;i<Lbracks.size();i++){
            System.out.println(Lbracks.get(i).toString());
            constExpNodes.get(i).print();
            System.out.println(Rbracks.get(i).toString());
        }
        System.out.println(assign.toString());
        constInitValNode.print();
        System.out.println("<ConstDef>");
    }
}
