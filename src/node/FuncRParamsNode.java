package node;

import Token.Token;

import java.util.List;

//FuncRParams → Exp { ',' Exp }
public class FuncRParamsNode {
    private List<Token> commas;
    private List<ExpNode>expNodes;

    public FuncRParamsNode(List<Token> commas, List<ExpNode> expNodes) {
        this.commas = commas;
        this.expNodes = expNodes;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        expNodes.get(0).print();
        for (int i=0;i<commas.size();i++){
            System.out.println(commas.get(i).toString());
            expNodes.get(i+1).print();
        }
        System.out.println("<FuncRParams>");
    }

    public List<Token> getCommas() {
        return commas;
    }

    public List<ExpNode> getExpNodes() {
        return expNodes;
    }
}
