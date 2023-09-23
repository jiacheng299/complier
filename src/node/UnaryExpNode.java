package node;

import front.Token;

//UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'| UnaryOp UnaryExp
public class UnaryExpNode {
    private PrimaryExpNode primaryExpNode;
    private Token ident;
    private Token lparent;
    private FuncRParamsNode funcRParamsNode;
    private Token rparent;
    private UnaryOpNode unaryOpNode;
    private UnaryExpNode unaryExpNode;

    public UnaryExpNode(PrimaryExpNode primaryExpNode, Token ident, Token lparent, FuncRParamsNode funcRParamsNode, Token rparent) {
        this.primaryExpNode = primaryExpNode;
        this.ident = ident;
        this.lparent = lparent;
        this.funcRParamsNode = funcRParamsNode;
        this.rparent = rparent;
    }
    public void print(){
        if (primaryExpNode!=null){
            primaryExpNode.print();
        } else if (unaryOpNode!=null) {
            unaryOpNode.print();
            unaryExpNode.print();
        }
        else {
            System.out.println(ident.toString());
            System.out.println(lparent.toString());
            if(funcRParamsNode!=null) funcRParamsNode.print();
            System.out.println(rparent.toString());
        }
        System.out.println("UnartExpNode");
    }
}
