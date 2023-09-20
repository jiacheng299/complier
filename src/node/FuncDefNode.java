package node;

import front.Token;

//FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
public class FuncDefNode {
    private FuncTypeNode funcTypeNode;
    private Token ident;
    private Token lparent;
    private FuncFParamsNode funcFParamsNode;
    private BlockNode blockNode;
    public FuncDefNode(FuncTypeNode funcTypeNode,Token ident,Token lparent,FuncFParamsNode funcFParamsNode,BlockNode blockNode) {
        this.funcTypeNode = funcTypeNode;
        this.ident = ident;
        this.lparent = lparent;
        this.funcFParamsNode=funcFParamsNode;
        this.blockNode = blockNode;
    }
    public void print(){
        funcTypeNode.print();
        System.out.println(ident.toString());
        System.out.println(lparent.toString());
        funcFParamsNode.print();
        blockNode.print();
        System.out.println("FuncDefNode");
    }
}
