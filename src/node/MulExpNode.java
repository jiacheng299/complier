package node;

import front.Token;

import java.util.List;

//MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
// MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp}
public class MulExpNode {
    private List<UnaryExpNode> unaryExpNodes;

    private List<Token> ops;

    public MulExpNode(List<UnaryExpNode> unaryExpNodes, List<Token> ops) {
        this.unaryExpNodes = unaryExpNodes;
        this.ops = ops;
    }

    public void print(){
        unaryExpNodes.get(0).print();
        for (int i=0;i<ops.size();i++){
            System.out.println("MulExpNode");
            System.out.println(ops.get(i).toString());
            unaryExpNodes.get(i+1).print();
        }
        System.out.println("MulExpNode");
    }
}
