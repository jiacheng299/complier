package node;

import Token.Token;

//PrimaryExp → '(' Exp ')' | LVal | Number
public class PrimaryExpNode {
    private Token lparent;
    private Token rparent;
    private ExpNode expNode;
    private LValNode lValNode;
    private NumberNode numberNode;

    public PrimaryExpNode(Token lparent, Token rparent, ExpNode expNode, LValNode lValNode, NumberNode numberNode) {
        this.lparent = lparent;
        this.rparent = rparent;
        this.expNode = expNode;
        this.lValNode = lValNode;
        this.numberNode = numberNode;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        if (lparent != null){
            System.out.println(lparent.toString());
            expNode.print();
            System.out.println(rparent.toString());
        } else if (lValNode != null) {
            lValNode.print();
        }
        else{
            numberNode.print();
        }
        System.out.println("<PrimaryExp>");
    }

    public Token getLparent() {
        return lparent;
    }

    public Token getRparent() {
        return rparent;
    }

    public ExpNode getExpNode() {
        return expNode;
    }

    public LValNode getlValNode() {
        return lValNode;
    }

    public NumberNode getNumberNode() {
        return numberNode;
    }
}
