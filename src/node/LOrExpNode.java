package node;

import front.Token;

import java.util.List;

//LOrExp → LAndExp | LOrExp '||' LAndExp
//改写文法 LOrExp -> LAndExp{'||' LAndExp}
public class LOrExpNode {

    private List<LAndExpNode> lAndExpNodes;
    private List<Token> ors;

    public LOrExpNode(List<LAndExpNode> lAndExpNodes, List<Token> ors) {
        this.lAndExpNodes = lAndExpNodes;
        this.ors = ors;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        lAndExpNodes.get(0).print();
        for (int i=0;i<ors.size();i++){
            System.out.println("<LOrExp>");
            System.out.println(ors.get(i).toString());
            lAndExpNodes.get(i+1).print();
        }
        System.out.println("<LOrExp>");
    }
}
