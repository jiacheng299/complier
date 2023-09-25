package node;

import front.Token;

import java.util.List;

//InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
public class InitValNode {
    private ExpNode expNode;
    private Token lbrace;
    private List<Token>commas;
    private List<InitValNode> initValNodes;
    private Token rbrace;

    public InitValNode(ExpNode expNode, Token lbrace, List<Token> commas, List<InitValNode> initValNodes, Token rbrace) {
        this.expNode = expNode;
        this.lbrace = lbrace;
        this.commas = commas;
        this.initValNodes = initValNodes;
        this.rbrace = rbrace;
    }

    public void print() {
        System.setOut(RedirectSystemOut.ps);
        if (expNode!=null){
            expNode.print();
        }
        else {
            System.out.println(lbrace.toString());
            if(initValNodes.size()>0){
                initValNodes.get(0).print();
            }
            for (int i=1;i<initValNodes.size();i++){
                System.out.println(commas.get(i-1).toString());
                initValNodes.get(i).print();
            }
            System.out.println(rbrace.toString());
        }
        System.out.println("<InitVal>");
    }
}
