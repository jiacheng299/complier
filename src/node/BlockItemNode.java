package node;
//BlockItem → Decl | Stmt
public class BlockItemNode {
    private DeclNode declnode;
    private StmtNode stmtnode;

    public BlockItemNode(DeclNode declnode, StmtNode stmtnode) {
        this.declnode = declnode;
        this.stmtnode = stmtnode;
    }
    public void print(){
        if(declnode!=null){
            declnode.print();
        }
        else{
            stmtnode.print();
        }
        System.out.println("BlockItemNode");
    }
}
