package node;

import front.Token;

//LOrExp → LAndExp | LOrExp '||' LAndExp
public class LOrExpNode {
    private LOrExpNode lOrExpNode;
    private LAndExpNode lAndExpNode;
    private Token or;

    public LOrExpNode(LOrExpNode lOrExpNode, LAndExpNode lAndExpNode, Token or) {
        this.lOrExpNode = lOrExpNode;
        this.lAndExpNode = lAndExpNode;
        this.or = or;
    }
    public void print(){
        if (lOrExpNode!=null){
            lOrExpNode.print();
            System.out.println(or.toString());
            lAndExpNode.print();
        }
        else{
            lAndExpNode.print();
        }
        System.out.println("LOrExpNode");
    }
}
