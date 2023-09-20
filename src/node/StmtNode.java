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
    private List<ForStmtNode> forStmtNodes;
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
    public StmtNode(StmtType stmtType,Token fortk,Token lparent,List<ForStmtNode>forStmtNodes,List<Token>semis,CondNode condNode,Token rparent,List<StmtNode> stmtNodes){
        this.stmtType=stmtType;
        this.fortk = fortk;
        this.lparent = lparent;
        this.forStmtNodes=forStmtNodes;
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
    public StmtNode(StmtType st ,Token printftk,Token lparent,Token rparent,List<Token>semis,Token formatStringtk,List<ExpNode>expNodes){
        this.stmtType=st;
        this.printftk=printftk;
        this.lparent=lparent;
        this.rparent=rparent;
        this.semis=semis;
        this.formatStringtk=formatStringtk;
        this.expNodes=expNodes;
    }
    public void print(){
        
    }
}
