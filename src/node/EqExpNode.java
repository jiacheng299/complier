package node;

import front.Token;

import java.util.List;

// EqExp → RelExp | EqExp ('==' | '!=') RelExp
//EqExp → RelExp {('==' | '!=') RelExp}
public class EqExpNode {
    private List<RelExpNode> relExpNode;
    private List<Token> eqlOrNeqs;

    public EqExpNode(List<RelExpNode> relExpNode, List<Token> eqlOrNeqs) {
        this.relExpNode = relExpNode;
        this.eqlOrNeqs = eqlOrNeqs;
    }


    public void print(){
        relExpNode.get(0).print();
        for (int i=0;i<eqlOrNeqs.size();i++){
            System.out.println("EqExpNode");
            System.out.println(eqlOrNeqs.get(i).toString());
            relExpNode.get(i+1).print();
        }
        System.out.println("EqExpNode");
    }
}
