package node;

import front.Token;

import java.util.List;

// RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
//RelExp → AddExp {('<' | '>' | '<=' | '>=') AddExp}
public class RelExpNode {
    private List<AddExpNode> addExpNodes;
    private List<Token> ops;

    public RelExpNode(List<AddExpNode> addExpNodes, List<Token> ops) {
        this.addExpNodes = addExpNodes;
        this.ops = ops;
    }

    public void print(){
        addExpNodes.get(0).print();
        for (int i=0;i<ops.size();i++){
            System.out.println("RelExpNode");
            System.out.println(ops.get(i).toString());
            addExpNodes.get(i+1).print();
        }
        System.out.println("RelExpNode");
    }
}
