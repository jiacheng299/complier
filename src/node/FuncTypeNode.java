package node;

import Token.Token;

//FuncType → 'void' | 'int'
public class FuncTypeNode {
    private Token voidtk;
    private Token inttk;
    public FuncTypeNode(Token voidtk,Token inttk) {
        this.voidtk = voidtk;
        this.inttk = inttk;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        if(voidtk!=null){
            System.out.println(voidtk.toString());
        }
        else{
            System.out.println(inttk.toString());
        }
        System.out.println("<FuncType>");
    }

    public Token getVoidtk() {
        return voidtk;
    }

    public Token getInttk() {
        return inttk;
    }
}
