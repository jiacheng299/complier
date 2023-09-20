package node;

import front.Token;

import java.util.List;

//Stmt → LVal '=' Exp ';' | [Exp] ';' | Block| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
//| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt| 'break' ';' | 'continue' ';'
//| 'return' [Exp] ';' | LVal '=' 'getint''('')'';'| 'printf''('FormatString{','Exp}')'';'
public class StmtNode {
    private LValNode lvalNode1;
    private Token assign1;
    private ExpNode expNode1;
    private Token semi1;
    private ExpNode expNode2;
    private Token semi2;
    private BlockNode blockNode;
    private Token iftk;
    private Token lparent1;
    private CondNode condNode1;
    private  Token rparent1;
    private StmtNode stmtNode1;
    private Token elsetk;
    private StmtNode stmtNode2;
    private Token fortk;
    private Token lparent2;
    private ForStmtNode forStmtNode1;
    private Token semi3;
    private CondNode condNode2;
    private Token semi4;
    private ForStmtNode forStmtNode2;
    private Token rparent2;
    private StmtNode stmtNode3;
    private Token breaktk;
    private Token semi5;
    private Token continuetk;
    private Token semi6;
    private Token returntk;
    private ExpNode expnode3;
    private LValNode lvalNode2;
    private Token assign2;
    private Token getinttk;
    private Token lparent3;
    private Token rparent3;
    private Token semi7;
    private Token printftk;
    private Token lparent4;
    private Token formatStringtk;
    private List<Token> commas;
    private List<ExpNode>expnodes;
    private Token rparent4;
    private Token semi8;

    public StmtNode(LValNode lvalNode1, Token assign1, ExpNode expNode1, Token semi1, ExpNode expNode2, Token semi2, BlockNode blockNode, Token iftk, Token lparent1, CondNode condNode1, Token rparent1, StmtNode stmtNode1, Token elsetk, StmtNode stmtNode2, Token fortk, Token lparent2, ForStmtNode forStmtNode1, Token semi3, CondNode condNode2, Token semi4, ForStmtNode forStmtNode2, Token rparent2, StmtNode stmtNode3, Token breaktk, Token semi5, Token continuetk, Token semi6, Token returntk, ExpNode expnode3, LValNode lvalNode2, Token assign2, Token getinttk, Token lparent3, Token rparent3, Token semi7, Token printftk, Token lparent4, Token formatStringtk, List<Token> commas, List<ExpNode> expnodes, Token rparent4, Token semi8) {
        this.lvalNode1 = lvalNode1;
        this.assign1 = assign1;
        this.expNode1 = expNode1;
        this.semi1 = semi1;
        this.expNode2 = expNode2;
        this.semi2 = semi2;
        this.blockNode = blockNode;
        this.iftk = iftk;
        this.lparent1 = lparent1;
        this.condNode1 = condNode1;
        this.rparent1 = rparent1;
        this.stmtNode1 = stmtNode1;
        this.elsetk = elsetk;
        this.stmtNode2 = stmtNode2;
        this.fortk = fortk;
        this.lparent2 = lparent2;
        this.forStmtNode1 = forStmtNode1;
        this.semi3 = semi3;
        this.condNode2 = condNode2;
        this.semi4 = semi4;
        this.forStmtNode2 = forStmtNode2;
        this.rparent2 = rparent2;
        this.stmtNode3 = stmtNode3;
        this.breaktk = breaktk;
        this.semi5 = semi5;
        this.continuetk = continuetk;
        this.semi6 = semi6;
        this.returntk = returntk;
        this.expnode3 = expnode3;
        this.lvalNode2 = lvalNode2;
        this.assign2 = assign2;
        this.getinttk = getinttk;
        this.lparent3 = lparent3;
        this.rparent3 = rparent3;
        this.semi7 = semi7;
        this.printftk = printftk;
        this.lparent4 = lparent4;
        this.formatStringtk = formatStringtk;
        this.commas = commas;
        this.expnodes = expnodes;
        this.rparent4 = rparent4;
        this.semi8 = semi8;
    }
    public void print(){
        
    }
}
