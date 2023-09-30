package node;
// Exp → AddExp
public class ExpNode {

    private AddExpNode addExpNode;

    public ExpNode( AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        addExpNode.print();
        System.out.println("<Exp>");
    }

    public AddExpNode getAddExpNode() {
        return addExpNode;
    }
}
