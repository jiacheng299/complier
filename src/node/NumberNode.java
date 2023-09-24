package node;

import front.Token;

//Number → IntConst
public class NumberNode {
    private Token number;

    public NumberNode(Token number) {
        this.number = number;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        System.out.println(number.toString());
        System.out.println("<Number>");
    }
}
