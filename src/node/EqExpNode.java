package node;

import front.Token;

// EqExp → RelExp | EqExp ('==' | '!=') RelExp
public class EqExpNode {
    private RelExpNode relExpNode;
    private EqExpNode eqExpNode;
    private Token eql;
    private Token neq;

    public EqExpNode(RelExpNode relExpNode, EqExpNode eqExpNode, Token eql, Token neq) {
        this.relExpNode = relExpNode;
        this.eqExpNode = eqExpNode;
        this.eql = eql;
        this.neq = neq;
    }
    public void print(){
        if (eqExpNode!=null){
            eqExpNode.print();
            if (eql!=null){
                System.out.println(eql.toString());
            }
            else{
                System.out.println(neq.toString());
            }
            relExpNode.print();
        }
        else{
            relExpNode.print();
        }
        System.out.println("EqExpNode");
    }
}
