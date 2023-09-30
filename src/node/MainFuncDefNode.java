package node;

import Token.Token;

// MainFuncDef → 'int' 'main' '(' ')' Block
public class MainFuncDefNode {
    private Token inttk;
    private Token maintk;
    private Token lparent;
    private Token rparent;
    private BlockNode blockNode;
    public MainFuncDefNode(Token inttk, Token maintk, Token lparent, Token rparent,BlockNode block) {
        this.inttk = inttk;
        this.maintk = maintk;
        this.lparent = lparent;
        this.rparent = rparent;
        this.blockNode = block;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        System.out.println(inttk.toString());
        System.out.println(maintk.toString());
        System.out.println(lparent.toString());
        System.out.println(rparent.toString());
        blockNode.print();
        System.out.println("<MainFuncDef>");
    }

    public Token getInttk() {
        return inttk;
    }

    public Token getMaintk() {
        return maintk;
    }

    public Token getLparent() {
        return lparent;
    }

    public Token getRparent() {
        return rparent;
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }
}
