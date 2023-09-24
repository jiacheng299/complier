package node;

import front.Token;

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
        funcFParamsNode.print();
        blockNode.print();
        System.out.println("<FuncDef>");
    }
}
