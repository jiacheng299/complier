package node;

import Token.Token;

import java.util.List;

//FuncFParams → FuncFParam { ',' FuncFParam }
public class FuncFParamsNode {

    private List<Token> commas;
    private List<FuncFParamNode> funcFParamsNodes;
    public FuncFParamsNode(List<Token> commas, List<FuncFParamNode> funcFParamsNodes) {
        this.commas = commas;
        this.funcFParamsNodes = funcFParamsNodes;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        funcFParamsNodes.get(0).print();
        for (int i=1;i<funcFParamsNodes.size();i++) {
            System.out.println(commas.get(i-1).toString());
            funcFParamsNodes.get(i).print();
        }
        System.out.println("<FuncFParams>");
    }

    public List<Token> getCommas() {
        return commas;
    }

    public List<FuncFParamNode> getFuncFParamsNodes() {
        return funcFParamsNodes;
    }
}
