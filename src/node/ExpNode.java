package node;
// Exp → AddExp
public class ExpNode {

    private AddExpNode addExpNode;

    public ExpNode( AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }
    public void print(){
        addExpNode.print();
        System.out.println("ExpNode");
    }
}
