package node;

import Token.Token;

//FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
public class FuncDefNode {
    private FuncTypeNode funcTypeNode;
    private Token ident;
    private Token lparent;
    private FuncFParamsNode funcFParamsNode;
    private Token rparent;
    private BlockNode blockNode;
    public FuncDefNode(FuncTypeNode funcTypeNode,Token ident,Token lparent,FuncFParamsNode funcFParamsNode,Token rparent,BlockNode blockNode) {
        this.funcTypeNode = funcTypeNode;
        this.ident = ident;
        this.lparent = lparent;
        this.funcFParamsNode=funcFParamsNode;
        this.rparent = rparent;
        this.blockNode = blockNode;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        funcTypeNode.print();
        System.out.println(ident.toString());
        System.out.println(lparent.toString());
        if (funcFParamsNode!=null) funcFParamsNode.print();
        System.out.println(rparent.toString());
        blockNode.print();
        System.out.println("<FuncDef>");
    }

    public FuncTypeNode getFuncTypeNode() {
        return funcTypeNode;
    }

    public Token getIdent() {
        return ident;
    }

    public Token getLparent() {
        return lparent;
    }

    public FuncFParamsNode getFuncFParamsNode() {
        return funcFParamsNode;
    }

    public Token getRparent() {
        return rparent;
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }
}
