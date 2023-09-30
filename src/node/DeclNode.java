package node;

public class DeclNode {
    private ConstDeclNode constDeclNode;
    private VarDeclNode varDeclNode;
    public DeclNode(ConstDeclNode constDeclNode,VarDeclNode varDeclNode){
        this.constDeclNode=constDeclNode;
        this.varDeclNode=varDeclNode;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        if(constDeclNode!=null){
            constDeclNode.print();
        }
        else{
            varDeclNode.print();
        }
       // System.out.println("DeclNode");
    }

    public ConstDeclNode getConstDeclNode() {
        return constDeclNode;
    }

    public VarDeclNode getVarDeclNode() {
        return varDeclNode;
    }
}
