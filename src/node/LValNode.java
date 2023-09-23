package node;

import front.Token;

import java.util.List;

//LVal → Ident {'[' Exp ']'}
public class LValNode {
    private Token ident;
    private List<Token> lbracks;
    private List<ExpNode>expNodes;
    private List<Token> rbracks;

    public LValNode(Token ident, List<Token> lbracks, List<ExpNode> expNodes, List<Token> rbracks) {
        this.ident = ident;
        this.lbracks = lbracks;
        this.expNodes = expNodes;
        this.rbracks = rbracks;
    }
    public void print(){
        System.out.println(ident.toString());
        for (int i=0;i< lbracks.size();i++){
            System.out.println(lbracks.get(i).toString());
            expNodes.get(i).print();
            System.out.println(rbracks.get(i).toString());
        }
    }
}
