package node;

import Token.Token;

import java.util.List;

// EqExp → RelExp | EqExp ('==' | '!=') RelExp
//EqExp → RelExp {('==' | '!=') RelExp}
public class EqExpNode {
    private List<RelExpNode> relExpNodes;
    private List<Token> eqlOrNeqs;

    public EqExpNode(List<RelExpNode> relExpNodes, List<Token> eqlOrNeqs) {
        this.relExpNodes = relExpNodes;
        this.eqlOrNeqs = eqlOrNeqs;
    }


    public void print(){
        System.setOut(RedirectSystemOut.ps);
        relExpNodes.get(0).print();
        for (int i=0;i<eqlOrNeqs.size();i++){
            System.out.println("<EqExp>");
            System.out.println(eqlOrNeqs.get(i).toString());
            relExpNodes.get(i+1).print();
        }
        System.out.println("<EqExp>");
    }

    public List<RelExpNode> getRelExpNodes() {
        return relExpNodes;
    }

    public List<Token> getEqlOrNeqs() {
        return eqlOrNeqs;
    }
}
