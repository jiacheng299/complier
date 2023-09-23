package node;
//ConstExp → AddExp
public class ConstExpNode {
    private AddExpNode addExpNode;

    public ConstExpNode(AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }
    public void print(){
        addExpNode.print();
        System.out.println("ConstExpNode");
    }
}
