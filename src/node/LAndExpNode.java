package node;

import front.Token;

import java.util.List;

//LAndExp → EqExp | LAndExp '&&' EqExp
//LAndExp → EqExp {'&&' EqExp}
public class LAndExpNode {
    private List<EqExpNode> eqExpNodes;
    private List<Token> ands;

    public LAndExpNode(List<EqExpNode> eqExpNodes, List<Token> ands) {
        this.eqExpNodes = eqExpNodes;
        this.ands = ands;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        eqExpNodes.get(0).print();
        for (int i=0;i<ands.size();i++){
            System.out.println("<LAndExp>");
            System.out.println(ands.get(i).toString());
            eqExpNodes.get(i+1).print();
        }
        System.out.println("<LAndExp>");
    }
}
