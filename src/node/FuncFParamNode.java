package node;

import front.Token;

import java.util.List;

//FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
public class FuncFParamNode {
    private BTypeNode btypenode;
    private Token ident;

    private List<Token> lbracks;
    private List<Token> rbracks;
    private List<ConstExpNode> constExpNodes;

    public FuncFParamNode(BTypeNode btypenode, Token ident,  List<Token> lbracks, List<Token> rbracks, List<ConstExpNode> constExpNodes) {
        this.btypenode = btypenode;
        this.ident = ident;
        this.lbracks = lbracks;
        this.rbracks = rbracks;
        this.constExpNodes = constExpNodes;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        btypenode.print();
        System.out.println(ident.toString());
        if(lbracks.size() > 0){
            System.out.println(lbracks.get(0).toString());
            System.out.println(rbracks.get(0).toString());
            for (int i=1;i<lbracks.size();i++){
                System.out.println(lbracks.get(i).toString());
                constExpNodes.get(i-1).print();
                System.out.println(rbracks.get(i).toString());
            }
        }
        System.out.println("<FuncFParam>");
    }
}
