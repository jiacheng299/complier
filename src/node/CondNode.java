package node;
//Cond → LOrExp
public class CondNode {
    private LOrExpNode lOrExpNode;

    public CondNode(LOrExpNode lOrExpNode) {
        this.lOrExpNode = lOrExpNode;
    }
    public void print(){
        lOrExpNode.print();
        System.out.println("CondNode");
    }
}
