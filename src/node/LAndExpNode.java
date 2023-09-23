package node;

import front.Token;

//LAndExp → EqExp | LAndExp '&&' EqExp
public class LAndExpNode {
    private EqExpNode eqExpNode;
    private LAndExpNode lAndExpNode;
    private Token and;

    public LAndExpNode(EqExpNode eqExpNode, LAndExpNode lAndExpNode, Token and) {
        this.eqExpNode = eqExpNode;
        this.lAndExpNode = lAndExpNode;
        this.and = and;
    }
    public void print(){
        if (lAndExpNode!=null){
            lAndExpNode.print();
            System.out.println(and.toString());
            eqExpNode.print();
        }
        else{
            eqExpNode.print();
        }
        System.out.println("LAndExpNode");
    }
}
