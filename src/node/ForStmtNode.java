package node;

import Token.Token;

//ForStmt → LVal '=' Exp
public class ForStmtNode {
    private LValNode lValNode;
    private Token assign;
    private ExpNode expNode;
    public ForStmtNode(LValNode lValNode,Token assign,ExpNode expNode){
        this.lValNode=lValNode;
        this.assign=assign;
        this.expNode=expNode;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        lValNode.print();
        System.out.println(assign.toString());
        expNode.print();
        System.out.println("<ForStmt>");
    }

    public LValNode getlValNode() {
        return lValNode;
    }

    public Token getAssign() {
        return assign;
    }

    public ExpNode getExpNode() {
        return expNode;
    }
}
