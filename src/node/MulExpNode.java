package node;

import front.Token;

//MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
public class MulExpNode {
    private UnaryExpNode unaryExpNode;
    private MulExpNode mulExpNode;
    private Token mult;
    private Token div;
    private Token mod;

    public MulExpNode(UnaryExpNode unaryExpNode, MulExpNode mulExpNode, Token mult, Token div, Token mod) {
        this.unaryExpNode = unaryExpNode;
        this.mulExpNode = mulExpNode;
        this.mult = mult;
        this.div = div;
        this.mod = mod;
    }
    public void print(){
        if (mulExpNode!=null) {
            mulExpNode.print();
            if (mult!=null){
                System.out.println(mult.toString());
            } else if (div!=null) {
                System.out.println(div.toString());
            }
            else {
                System.out.println(mod.toString());
            }
            unaryExpNode.print();
        }
        else{
            unaryExpNode.print();
        }
        System.out.println("MulExpNode");
    }
}
