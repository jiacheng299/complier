package node;
import Token.Token;

import java.util.List;

//ConstInitVal → ConstExp| '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
public class ConstInitValNode {
    private ConstExpNode constExpNode;
    private Token Lbrace;
    private List<ConstInitValNode>constInitValNodes;
    private List<Token> commas;
    private Token Rbrace;
    public ConstInitValNode(ConstExpNode constExpNode,Token Lbrace,List<ConstInitValNode>constInitValNodes,List<Token> commas,Token Rbrace){
        this.constExpNode=constExpNode;
        this.Lbrace=Lbrace;
        this.constInitValNodes=constInitValNodes;
        this.commas=commas;
        this.Rbrace=Rbrace;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        if(constExpNode!=null)  constExpNode.print();
        else {
            System.out.println(Lbrace.toString());
            if(constInitValNodes.size()>0){
                constInitValNodes.get(0).print();
                for (int i=1;i<constInitValNodes.size();i++){
                    System.out.println(commas.get(i-1));
                    constInitValNodes.get(i).print();
                }
            }
            System.out.println(Rbrace.toString());
        }
        System.out.println("<ConstInitVal>");
    }

    public ConstExpNode getConstExpNode() {
        return constExpNode;
    }

    public Token getLbrace() {
        return Lbrace;
    }

    public List<ConstInitValNode> getConstInitValNodes() {
        return constInitValNodes;
    }

    public List<Token> getCommas() {
        return commas;
    }

    public Token getRbrace() {
        return Rbrace;
    }
}
