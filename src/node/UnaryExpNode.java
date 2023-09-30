package node;

import Token.Token;

//UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'| UnaryOp UnaryExp
public class UnaryExpNode {
    private PrimaryExpNode primaryExpNode;
    private Token ident;
    private Token lparent;
    private FuncRParamsNode funcRParamsNode;
    private Token rparent;
    private UnaryOpNode unaryOpNode;
    private UnaryExpNode unaryExpNode;

    public UnaryExpNode(PrimaryExpNode primaryExpNode, Token ident, Token lparent, FuncRParamsNode funcRParamsNode, Token rparent,UnaryOpNode unaryOpNode,UnaryExpNode unaryExpNode) {
        this.primaryExpNode = primaryExpNode;
        this.ident = ident;
        this.lparent = lparent;
        this.funcRParamsNode = funcRParamsNode;
        this.rparent = rparent;
        this.unaryOpNode = unaryOpNode;
        this.unaryExpNode = unaryExpNode;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
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
        System.out.println("<UnaryExp>");
    }

    public PrimaryExpNode getPrimaryExpNode() {
        return primaryExpNode;
    }

    public Token getIdent() {
        return ident;
    }

    public Token getLparent() {
        return lparent;
    }

    public FuncRParamsNode getFuncRParamsNode() {
        return funcRParamsNode;
    }

    public Token getRparent() {
        return rparent;
    }

    public UnaryOpNode getUnaryOpNode() {
        return unaryOpNode;
    }

    public UnaryExpNode getUnaryExpNode() {
        return unaryExpNode;
    }
}
