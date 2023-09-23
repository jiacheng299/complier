package node;

import front.Token;

//PrimaryExp → '(' Exp ')' | LVal | Number
public class PrimaryExpNode {
    private Token lparent;
    private Token rparent;
    private ExpNode expNode;
    private LValNode lValNode;
    private NumberNode numberNode;
    public PrimaryExpNode(Token lparent, Token rparent, ExpNode expNode) {
        this.lparent = lparent;
        this.rparent = rparent;
        this.expNode = expNode;
    }
}
