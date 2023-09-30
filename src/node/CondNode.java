package node;
//Cond → LOrExp
public class CondNode {
    private LOrExpNode lOrExpNode;

    public CondNode(LOrExpNode lOrExpNode) {
        this.lOrExpNode = lOrExpNode;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        lOrExpNode.print();
        System.out.println("<Cond>");
    }

    public LOrExpNode getlOrExpNode() {
        return lOrExpNode;
    }
}
