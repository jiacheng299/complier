package node;

import Token.Token;

//UnaryOp → '+' | '−' | '!'
public class UnaryOpNode {
    private Token plus;
    private Token minu;
    private Token not;

    public UnaryOpNode(Token plus, Token minu, Token not) {
        this.plus = plus;
        this.minu = minu;
        this.not = not;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        if(this.plus!=null){
            System.out.println(plus.toString());
        } else if (this.minu != null) {
            System.out.println(minu.toString());
        }
        else{
            System.out.println(not.toString());
        }
        System.out.println("<UnaryOp>");
    }

    public Token getPlus() {
        return plus;
    }

    public Token getMinu() {
        return minu;
    }

    public Token getNot() {
        return not;
    }
}
