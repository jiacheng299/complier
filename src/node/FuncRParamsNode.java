package node;

import front.Token;

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
        expNodes.get(0).print();
        for (int i=0;i<commas.size();i++){
            System.out.println(commas.get(i).toString());
            expNodes.get(i+1).print();
        }
        System.out.println("FuncRParams");
    }
}
