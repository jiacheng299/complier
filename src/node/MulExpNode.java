package node;

import Token.Token;

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
        System.setOut(RedirectSystemOut.ps);
        unaryExpNodes.get(0).print();
        for (int i=0;i<ops.size();i++){
            System.out.println("<MulExp>");
            System.out.println(ops.get(i).toString());
            unaryExpNodes.get(i+1).print();
        }
        System.out.println("<MulExp>");
    }

    public List<UnaryExpNode> getUnaryExpNodes() {
        return unaryExpNodes;
    }

    public List<Token> getOps() {
        return ops;
    }
}
