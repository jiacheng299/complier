package node;

import front.Token;

// RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
public class RelExpNode {
    private AddExpNode addExpNode;
    private RelExpNode relExpNode;
    private Token lss;
    private Token gre;
    private Token leq;
    private Token geq;

    public RelExpNode(AddExpNode addExpNode, RelExpNode relExpNode, Token lss, Token gre, Token leq, Token geq) {
        this.addExpNode = addExpNode;
        this.relExpNode = relExpNode;
        this.lss = lss;
        this.gre = gre;
        this.leq = leq;
        this.geq = geq;
    }
    public void print(){
        if(relExpNode!=null){
            relExpNode.print();
            if (lss!=null){
                System.out.println(lss.toString());
            } else if (gre!=null) {
                System.out.println(gre.toString());
            }
            else if (leq!=null) {
                System.out.println(leq.toString());
            }
            else if (geq!=null) {
                System.out.println(geq.toString());
            }
            addExpNode.print();
        }
        else{
            addExpNode.print();
        }
        System.out.println();
    }
}
