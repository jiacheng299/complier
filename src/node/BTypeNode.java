package node;

import Token.Token;
//BType → 'int'
public class BTypeNode {
    private Token inttk;
    public BTypeNode(Token inttk){
        this.inttk=inttk;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        System.out.println(inttk.toString());
        //System.out.println("BType");
    }

    public Token getInttk() {
        return inttk;
    }
}
