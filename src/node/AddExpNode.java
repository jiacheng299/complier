package node;

import front.Token;

import java.util.List;

//AddExp → MulExp | AddExp ('+' | '−') MulExp
//改写文法为AddExp → MulExp {('+' | '−') MulExp}
public class AddExpNode {

    private List<Token> pluseOrminus;

    private List<MulExpNode> mulExpNodes;

    public AddExpNode( List<Token> pluseOrminus, List<MulExpNode> mulExpNodes) {
        this.pluseOrminus=pluseOrminus;
        this.mulExpNodes = mulExpNodes;
    }
    public void print(){
        mulExpNodes.get(0).print();
            for (int i=0;i<pluseOrminus.size();i++){
                System.out.println("AddExp");
                System.out.println(pluseOrminus.get(i).toString());
                mulExpNodes.get(i+1).print();
            }

        System.out.println("AddExp");
    }
}
