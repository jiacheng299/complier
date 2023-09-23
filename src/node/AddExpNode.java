package node;

import front.Token;

import java.util.List;

//AddExp → MulExp | AddExp ('+' | '−') MulExp
//改写文法为AddExp → MulExp {('+' | '−') MulExp}
public class AddExpNode {
    private MulExpNode mulExpNode;
    private List<Token> pluseOrminus;

    private List<MulExpNode> mulExpNodes;

    public AddExpNode(MulExpNode mulExpNode, List<Token> pluseOrminus, List<MulExpNode> mulExpNodes) {
        this.mulExpNode = mulExpNode;
        this.pluseOrminus=pluseOrminus;
        this.mulExpNodes = mulExpNodes;
    }
    public void print(){
        if (mulExpNode!=null){
            mulExpNode.print();
        }
        else{
            for (int i=0;i<mulExpNodes.size();i++){
                System.out.println("AddExp");
                System.out.println(pluseOrminus.get(i).toString());
                mulExpNodes.get(i).print();
            }
        }
        System.out.println("AddExp");
    }
}
