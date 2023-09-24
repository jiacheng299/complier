package node;

import front.Token;

import java.util.List;

//Stmt → LVal '=' Exp ';'
// | [Exp] ';'
// | Block
// | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
//| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
// | 'break' ';'
// | 'continue' ';'
//| 'return' [Exp] ';'
// | LVal '=' 'getint''('')'';'
// | 'printf''('FormatString{','Exp}')'';'
public class StmtNode {
    private LValNode lvalnode;
    private Token assign;
    private ExpNode expNode;
    private List<Token> semis;
    private BlockNode blockNode;
    private Token iftk;
    private Token lparent;
    private Token rparent;
    private CondNode condNode;
    private List<StmtNode> stmtNodes;
    private Token elsetk;
    private Token fortk;
    private ForStmtNode forStmt1=null,forStmt2=null;
    private Token breaktkOrcontinuetk;
    private Token returntk;
    private Token getinttk;
    private Token printftk;
    private Token formatStringtk;
    private List<Token> commas;
    private List<ExpNode>expNodes;
    private StmtType stmtType;
    public enum StmtType{
        LvalAssignExp,Exp,Block,If,For,Break,Continue,Return,LvalAssignGetint,Printf
    }
    // Stmt → LVal '=' Exp ';'
    public StmtNode(StmtType stmtType, LValNode lvalnode,Token assign,ExpNode expNode,List<Token>semis) {
        this.stmtType=stmtType;
        this.lvalnode = lvalnode;
        this.assign = assign;
        this.expNode = expNode;
        this.semis = semis;
    }
    //| [Exp] ';'
    public StmtNode(StmtType stmtType,ExpNode expNode,List<Token>semis) {
        this.stmtType=stmtType;
        this.expNode = expNode;
        this.semis = semis;
    }
    //| Block
    public StmtNode(StmtType stmtType,BlockNode blockNode){
        this.blockNode = blockNode;
    }
    //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
    public StmtNode(StmtType stmtType,Token iftk,Token lparent,CondNode condNode,Token rparent ,List<StmtNode> stmtNodes,Token elsetk){
        this.stmtType=stmtType;
        this.condNode = condNode;
        this.iftk = iftk;
        this.lparent = lparent;
        this.rparent = rparent;
        this.stmtNodes = stmtNodes;
        this.elsetk=elsetk;
    }
    //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    public StmtNode(StmtType stmtType,Token fortk,Token lparent,ForStmtNode forStmt1,ForStmtNode forStmt2,List<Token>semis,CondNode condNode,Token rparent,List<StmtNode> stmtNodes){
        this.stmtType=stmtType;
        this.fortk = fortk;
        this.lparent = lparent;
        this.forStmt1=forStmt1;
        this.forStmt2=forStmt2;
        this.semis = semis;
        this.condNode = condNode;
        this.rparent = rparent;
        this.stmtNodes = stmtNodes;
    }
    //| 'break' ';'| 'continue' ';'
    public StmtNode(StmtType stmtType,Token breaktkOrcontinuetk,List<Token>semis){
        this.stmtType=stmtType;
        this.breaktkOrcontinuetk = breaktkOrcontinuetk;
        this.semis = semis;
    }
    //| 'return' [Exp] ';'
    public StmtNode(StmtType st,Token returntk,ExpNode expNode,List<Token>semis){
        this.stmtType=st;
        this.returntk = returntk;
        this.expNode=expNode;
        this.semis = semis;
    }
    //| LVal '=' 'getint''('')'';'
    public StmtNode(StmtType st ,LValNode lval,Token assign,Token getinttk,Token lparent,Token rparent,List<Token>semis){
        this.stmtType=st;
        this.lvalnode=lval;
        this.assign=assign;
        this.getinttk=getinttk;
        this.lparent=lparent;
        this.rparent=rparent;
        this.semis=semis;
    }
    //| 'printf''('FormatString{','Exp}')'';'
    public StmtNode(StmtType st ,Token printftk,Token lparent,Token rparent,List<Token>semis,Token formatStringtk,List<ExpNode>expNodes,List<Token>commas){
        this.stmtType=st;
        this.printftk=printftk;
        this.lparent=lparent;
        this.rparent=rparent;
        this.semis=semis;
        this.formatStringtk=formatStringtk;
        this.commas=commas;
        this.expNodes=expNodes;
    }
    public void print(){
        System.setOut(RedirectSystemOut.ps);
        //LvalAssignExp,Exp,Block,If,For,Break,Continue,Return,LvalAssignGetint,Printf
        if (stmtType==StmtType.LvalAssignExp){
            lvalnode.print();
            System.out.println(assign.toString());
            expNode.print();
            System.out.println(semis.get(0).toString());
        } else if (stmtType==StmtType.Exp) {
            if (expNode!=null) expNode.print();
            System.out.println(semis.get(0).toString());
        } else if (stmtType==StmtType.Block) {
            blockNode.print();
        } else if (stmtType==StmtType.If) {
            System.out.println(iftk.toString());
            System.out.println(lparent.toString());
            condNode.print();
            System.out.println(rparent.toString());
            stmtNodes.get(0).print();
            if (elsetk!=null){
                System.out.println(elsetk.toString());
                stmtNodes.get(1).print();
            }
        } else if (stmtType==StmtType.For) {
            System.out.println(fortk.toString());
            System.out.println(lparent.toString());
            if (forStmt1!=null){
                forStmt1.print();
            }
            System.out.println(semis.get(0).toString());
            if (condNode!=null) condNode.print();
            System.out.println(semis.get(1).toString());
            if (forStmt2!=null){
                forStmt2.print();
            }
            System.out.println(rparent.toString());
            stmtNodes.get(0).print();
        } else if (stmtType==StmtType.Break||stmtType==StmtType.Continue) {
            System.out.println(breaktkOrcontinuetk.toString());
            System.out.println(semis.get(0).toString());
        } else if (stmtType==StmtType.Return) {
            System.out.println(returntk.toString());
            if (expNode!=null){
                expNode.print();
            }
            System.out.println(semis.get(0).toString());
        } else if (stmtType==StmtType.LvalAssignGetint) {
            lvalnode.print();
            System.out.println(assign.toString());
            System.out.println(getinttk.toString());
            System.out.println(lparent.toString());
            System.out.println(rparent.toString());
            System.out.println(semis.get(0).toString());
        } else if (stmtType==StmtType.Printf) {
            System.out.println(printftk.toString());
            System.out.println(lparent.toString());
            System.out.println(formatStringtk.toString());
            for (int i=0;i<commas.size();i++){
                System.out.println(commas.get(i).toString());
                expNodes.get(i).print();
            }
            System.out.println(rparent.toString());
            System.out.println(semis.get(0).toString());
        }
        System.out.println("<Stmt>");
    }
}
