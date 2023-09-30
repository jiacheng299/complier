package node;

import Token.Token;

import java.util.List;

//Block → '{' { BlockItem } '}'
public class BlockNode {
    private Token lbrace;
    private List<BlockItemNode>blockItemNodes;
    private Token rbrace;

    public BlockNode(Token lbrace, List<BlockItemNode> blockItemNodes, Token rbrace) {
        this.lbrace = lbrace;
        this.blockItemNodes = blockItemNodes;
        this.rbrace = rbrace;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        System.out.println(lbrace.toString());
        for (BlockItemNode node : blockItemNodes){
            node.print();
        }
        System.out.println(rbrace.toString());
        System.out.println("<Block>");
    }

    public Token getLbrace() {
        return lbrace;
    }

    public List<BlockItemNode> getBlockItemNodes() {
        return blockItemNodes;
    }

    public Token getRbrace() {
        return rbrace;
    }
}
