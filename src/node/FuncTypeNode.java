package node;

import front.Token;

//FuncType → 'void' | 'int'
public class FuncTypeNode {
    private Token voidtk;
    private Token inttk;
    public FuncTypeNode(Token voidtk,Token inttk) {
        this.voidtk = voidtk;
        this.inttk = inttk;
    }
    public void print(){
        if(voidtk!=null){
            System.out.println(voidtk.toString());
        }
        else{
            System.out.println(inttk.toString());
        }
        System.out.println("FuncTypeNode");
    }
}
