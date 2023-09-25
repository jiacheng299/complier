package front;

import node.*;

import java.util.ArrayList;
import java.util.List;
import error.*;
public class Parser {
    private List<Token> tokens;
    private int currentTokenIndex;
    private CompUnitNode entrance;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }
    private void error(){
        System.out.println("error");
    }
    // 解析入口方法
    public void parse() {
        entrance=compUnit();
    }
    public CompUnitNode getEntrance(){
        return entrance;
    }
    // CompUnit规则
    private CompUnitNode compUnit() {
        // CompUnit -> {Decl} {FuncDef} MainFuncDef
        List<DeclNode>declNodes = new ArrayList<>();
        List<FuncDefNode>funcDefNodes=new ArrayList<>();
        MainFuncDefNode mainFuncDefNode=null;
        while (hasNextToken()) {
            if (getCurrentToken().getType()==TokenType.CONSTTK||(getCurrentToken().getType()==TokenType.INTTK&&tokens.get(currentTokenIndex+2).getType()!=TokenType.LPARENT)) {
                declNodes.add(decl());
            } else if (getCurrentToken().getType()==TokenType.VOIDTK||(getCurrentToken().getType()==TokenType.INTTK&&tokens.get(currentTokenIndex+1).getType()==TokenType.IDENFR)) {
                funcDefNodes.add(funcDef());
            } else {
                break;
            }
        }
        mainFuncDefNode=mainFuncDef();
        return new CompUnitNode(declNodes,funcDefNodes,mainFuncDefNode);
    }

    // Decl规则
    private DeclNode decl() {
        // Decl → ConstDecl | VarDecl
        ConstDeclNode constDeclNode=null;
        VarDeclNode varDeclNode=null;
        if(getCurrentToken().getType()==TokenType.CONSTTK){
            constDeclNode=constDecl();
        } else if (getCurrentToken().getType()==TokenType.INTTK) {
            varDeclNode=varDecl();
        }
        else{
            error();
        }
        return new DeclNode(constDeclNode,varDeclNode);
    }

    private ConstDeclNode constDecl() {
        //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        Token consttk=match(TokenType.CONSTTK);
        BTypeNode bTypeNode=Btype();
        ConstDefNode constDefNode=ConstDef();
        List<Token> commas=new ArrayList<>();
        List<ConstDefNode> constDefNodes=new ArrayList<>();
        while(getCurrentToken().getType()==TokenType.COMMA){
            commas.add(match(TokenType.COMMA));
            constDefNodes.add(ConstDef());
        }
        Token semi=match(TokenType.SEMICN);
        return new ConstDeclNode(consttk,bTypeNode,constDefNode,commas,constDefNodes,semi);
    }
    private BTypeNode Btype() {
        // BType → 'int'
        Token inttk=match(TokenType.INTTK);
        return new BTypeNode(inttk);
    }
    private ConstDefNode ConstDef() {
        // ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        Token ident=match(TokenType.IDENFR);
        List<Token> lbracks=new ArrayList<>();
        List<ConstExpNode> constExpNodes=new ArrayList<>();
        List<Token> rbracks=new ArrayList<>();
        Token assign=null;
        ConstInitValNode constInitValNode=null;
        while (getCurrentToken().getType()==TokenType.LBRACK){
            lbracks.add(match(TokenType.LBRACK));
            constExpNodes.add(ConstExp());
            rbracks.add(match(TokenType.RBRACK));
        }
        assign=match(TokenType.ASSIGN);
        constInitValNode=ConstInitVal();
        return new ConstDefNode(ident,lbracks,constExpNodes,rbracks,assign,constInitValNode);
    }

    private ConstInitValNode ConstInitVal() {
        //ConstInitVal → ConstExp| '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        ConstExpNode constExpNode=null;
        Token lbrace=null;
        List<Token> commas=new ArrayList<>();
        List<ConstInitValNode>constInitValNodes=new ArrayList<>();
        Token rbrace=null;
        if (getCurrentToken().getType()==TokenType.LBRACE){
            lbrace=match(TokenType.LBRACE);
            if (getCurrentToken().getType()==TokenType.RBRACE){
            }
            else{
                constInitValNodes.add(ConstInitVal());
                while(getCurrentToken().getType()==TokenType.COMMA){
                    commas.add(match(TokenType.COMMA));
                    constInitValNodes.add(ConstInitVal());
                }
            }
            rbrace=match(TokenType.RBRACE);
        }
        else {
            constExpNode=ConstExp();
        }
        return new ConstInitValNode(constExpNode,lbrace,constInitValNodes,commas,rbrace);
    }
    private VarDeclNode varDecl() {
        // VarDecl → BType VarDef { ',' VarDef } ';'
        BTypeNode bTypeNode=Btype();
        VarDefNode varDefNode=VarDef();
        List<Token> commas=new ArrayList<>();
        List<VarDefNode> varDefNodes=new ArrayList<>();
        Token semi=null;
        while(getCurrentToken().getType()==TokenType.COMMA){
            commas.add(match(TokenType.COMMA));
            varDefNodes.add(VarDef());
        }
        semi=match(TokenType.SEMICN);
        return new VarDeclNode(bTypeNode,varDefNode,commas,varDefNodes,semi);
    }

    private VarDefNode VarDef() {
    //VarDef → Ident { '[' ConstExp ']' }| Ident { '[' ConstExp ']' } '=' InitVal
        Token ident=match(TokenType.IDENFR);
        List<Token> lbracks=new ArrayList<>();
        List<ConstExpNode> constExpNodes=new ArrayList<>();
        List<Token> rbracks=new ArrayList<>();
        Token assign=null;
        InitValNode intiValue=null;
        while(getCurrentToken().getType()==TokenType.LBRACK) {
            lbracks.add(match(TokenType.LBRACK));
            constExpNodes.add(ConstExp());
            rbracks.add(match(TokenType.RBRACK));
        }
        if (getCurrentToken().getType()==TokenType.ASSIGN){
            assign=match(TokenType.ASSIGN);
            intiValue=InitVal();
        }

        return new VarDefNode(ident,lbracks,constExpNodes,rbracks,assign,intiValue);
    }

    private InitValNode InitVal() {
        //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        ExpNode expNode = null;
        Token lbrace=null;
        List<Token> commas=new ArrayList<>();
        List<InitValNode> initValNodes=new ArrayList<>();
        Token rbrace=null;
        if(getCurrentToken().getType()==TokenType.LBRACE){
            lbrace=match(TokenType.LBRACE);
            if(getCurrentToken().getType()==TokenType.RBRACE){

            }
            else{
                initValNodes.add(InitVal());
                while(getCurrentToken().getType()==TokenType.COMMA){
                    commas.add(match(TokenType.COMMA));
                    initValNodes.add(InitVal());
                }
                rbrace = match(TokenType.RBRACE);
            }
        }
        else{
            expNode=Exp();
        }
        return new InitValNode(expNode,lbrace,commas,initValNodes,rbrace);
    }
    private FuncDefNode funcDef() {
        // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        FuncTypeNode funcType =FuncType();
        Token ident=match(TokenType.IDENFR);
        Token lparent=match(TokenType.LPARENT);
        FuncFParamsNode funcFParamsNode =null;
        Token rparent=null;
        if(getCurrentToken().getType() == TokenType.RPARENT) {
            rparent = match(TokenType.RPARENT);
        }
        else{
            funcFParamsNode =FuncFParams();
            rparent = match(TokenType.RPARENT);
        }
        BlockNode blockNode =Block();
        return new FuncDefNode(funcType,ident,lparent,funcFParamsNode, rparent,blockNode);
    }
    private MainFuncDefNode mainFuncDef() {
        //MainFuncDef → 'int' 'main' '(' ')' Block
        Token inttk=match(TokenType.INTTK);
        Token maintk=match(TokenType.MAINTK);
        Token lparent=match(TokenType.LPARENT);
        Token rparent=match(TokenType.RPARENT);
        BlockNode blockNode =Block();
        return new MainFuncDefNode(inttk,maintk,lparent,rparent,blockNode);
    }
    private FuncTypeNode FuncType() {
        //FuncType → 'void' | 'int'
        Token voidtk=null;
        Token inttk=null;
        if(getCurrentToken().getType()!=TokenType.INTTK){
            voidtk=match(TokenType.VOIDTK);
        }
        else{
            inttk=match(TokenType.INTTK);
        }
        return new FuncTypeNode(voidtk, inttk);
    }
    private FuncFParamsNode FuncFParams() {
        //FuncFParams → FuncFParam { ',' FuncFParam }
        //FuncFParamNode funcFParamNode =FuncFParam();
        List<Token> commas=new ArrayList<>();
        List<FuncFParamNode> funcFParamNodes=new ArrayList<>();
        funcFParamNodes.add(FuncFParam());
        while(getCurrentToken().getType()==TokenType.COMMA){
            commas.add(match(TokenType.COMMA));
            funcFParamNodes.add(FuncFParam());
        }
        return new FuncFParamsNode(commas, funcFParamNodes);
    }

    private FuncFParamNode FuncFParam() {
        // FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        BTypeNode btypeNode=Btype();
        Token ident=match(TokenType.IDENFR);
        List<Token> lbracks=new ArrayList<>();
        List<Token> rbracks=new ArrayList<>();
        List<ConstExpNode>constExpNodes=new ArrayList<>();
        if (getCurrentToken().getType()==TokenType.LBRACK){
            lbracks.add(match(TokenType.LBRACK));
            rbracks.add(match(TokenType.RBRACK));
            while(getCurrentToken().getType()==TokenType.LBRACK){
                lbracks.add(match(TokenType.LBRACK));
                constExpNodes.add(ConstExp());
                rbracks.add(match(TokenType.RBRACK));
            }
        }
        return new FuncFParamNode(btypeNode,ident,lbracks,rbracks,constExpNodes);
    }

    private BlockNode Block() {
        //Block → '{' { BlockItem } '}'
        Token lbrace=match(TokenType.LBRACE);
        List<BlockItemNode> blockItemNodes=new ArrayList<>();
        while (getCurrentToken().getType()!=TokenType.RBRACE){
            blockItemNodes.add(BlockItem());
        }
        Token rbrace=match(TokenType.RBRACE);
        return new BlockNode(lbrace,blockItemNodes,rbrace);
    }

    private BlockItemNode BlockItem() {
        //BlockItem → Decl | Stmt
        DeclNode declNode=null;
        StmtNode st=null;
        if(getCurrentToken().getType()==TokenType.CONSTTK||getCurrentToken().getType()==TokenType.INTTK){
            declNode=decl();
        }
        else{
            st=Stmt();
        }
        return new BlockItemNode(declNode,st);
    }

    private StmtNode Stmt() {
        LValNode lvalNode=null;
        ExpNode expNode=null;
        BlockNode blockNode=null;
        CondNode condNode=null;
        Token format=null;
        Token assign=null;
        Token getinttk=null;
        Token lparent=null;
        Token rparent=null;
        Token iftk=null;
        Token elsetk=null;
        Token fortk=null;
        Token returntk=null;
        Token breakOrContinuetk=null;
        Token printtk=null;
        ForStmtNode forStmtNode1=null,forStmtNode2=null;
        List<Token> commas=new ArrayList<>();
        List<ExpNode> expNodes=new ArrayList<>();
        List<Token> semis=new ArrayList<>();
        List<StmtNode> stmtNodes = new ArrayList<>();

        //Lval
        if (getCurrentToken().getType()==TokenType.IDENFR&&(tokens.get(currentTokenIndex+1).getType()==TokenType.ASSIGN||tokens.get(currentTokenIndex+1).getType()==TokenType.LBRACK)){
            lvalNode=LVal();
            assign=match(TokenType.ASSIGN);
            //| LVal '=' 'getint''('')'';'
            if (getCurrentToken().getType()==TokenType.GETINTTK){
                getinttk=match(TokenType.GETINTTK);
                lparent=match(TokenType.LPARENT);
                rparent=match(TokenType.RPARENT);
                semis.add(match(TokenType.SEMICN));
                return new StmtNode(StmtNode.StmtType.LvalAssignGetint,lvalNode,assign,getinttk,lparent,rparent,semis);
            }
            else{
                //Stmt → LVal '=' Exp ';'
                expNode=Exp();
                semis.add(match(TokenType.SEMICN));
                return new StmtNode(StmtNode.StmtType.LvalAssignExp,lvalNode,assign,expNode,semis);
            }
        } else if (getCurrentToken().getType()==TokenType.SEMICN||getCurrentToken().getType()==TokenType.IDENFR||getCurrentToken().getType() == TokenType.LPARENT||getCurrentToken().getType()==TokenType.INTCON) {
            //| [Exp] ';'
            if(getCurrentToken().getType() == TokenType.SEMICN){
                semis.add(match(TokenType.SEMICN));
            }
            else{
                expNode=Exp();
                semis.add(match(TokenType.SEMICN));
            }
            return new StmtNode(StmtNode.StmtType.Exp,expNode,semis);
        }else if (getCurrentToken().getType()==TokenType.LBRACE) {
            //| Block
            blockNode=Block();
            return new StmtNode(StmtNode.StmtType.Block,blockNode);
        }else if (getCurrentToken().getType()==TokenType.IFTK) {
            //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            iftk=match(TokenType.IFTK);
            lparent=match(TokenType.LPARENT);
            condNode=Cond();
            rparent=match(TokenType.RPARENT);
            stmtNodes.add(Stmt());
            if (getCurrentToken().getType()==TokenType.ELSETK){
                elsetk=match(TokenType.ELSETK);
                stmtNodes.add(Stmt());
            }
            return new StmtNode(StmtNode.StmtType.If,iftk,lparent,condNode,rparent,stmtNodes,elsetk);
        }else if (getCurrentToken().getType()==TokenType.FORTK) {
            //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
            fortk=match(TokenType.FORTK);
            lparent=match(TokenType.LPARENT);
            if (getCurrentToken().getType()!=TokenType.SEMICN){
                forStmtNode1=ForStmt();
            }
            semis.add(match(TokenType.SEMICN));
            if (getCurrentToken().getType()!=TokenType.SEMICN){
                condNode=Cond();
            }
            semis.add(match(TokenType.SEMICN));
            if (getCurrentToken().getType()!=TokenType.RPARENT){
                forStmtNode2=ForStmt();
            }
            rparent=match(TokenType.RPARENT);
            stmtNodes.add(Stmt());
            return new StmtNode(StmtNode.StmtType.For,fortk,lparent,forStmtNode1,forStmtNode2,semis,condNode,rparent,stmtNodes);
        }else if (getCurrentToken().getType()==TokenType.BREAKTK||getCurrentToken().getType()==TokenType.CONTINUETK) {
            //| 'break' ';' | 'continue' ';'
            if(getCurrentToken().getType()==TokenType.BREAKTK){
                breakOrContinuetk=match(TokenType.BREAKTK);
                semis.add(match(TokenType.SEMICN));
                return new StmtNode(StmtNode.StmtType.Break,breakOrContinuetk,semis);
            }
            else{
                breakOrContinuetk=match(TokenType.CONTINUETK);
                semis.add(match(TokenType.SEMICN));
                return new StmtNode(StmtNode.StmtType.Continue,breakOrContinuetk,semis);
            }
        }else if (getCurrentToken().getType()==TokenType.RETURNTK) {
            //| 'return' [Exp] ';'
            returntk=match(TokenType.RETURNTK);
            if(getCurrentToken().getType()!=TokenType.SEMICN){
                expNode=Exp();
            }
            semis.add(match(TokenType.SEMICN));
            return new StmtNode(StmtNode.StmtType.Return,returntk,expNode,semis);
        }else if (getCurrentToken().getType()==TokenType.PRINTFTK) {
            //| 'printf''('FormatString{','Exp}')'';'
            printtk=match(TokenType.PRINTFTK);
            lparent=match(TokenType.LPARENT);
            format=match(TokenType.STRCON);
            while(getCurrentToken().getType()==TokenType.COMMA){
                commas.add(match(TokenType.COMMA));
                expNodes.add(Exp());
            }
            rparent=match(TokenType.RPARENT);
            semis.add(match(TokenType.SEMICN));
            return new StmtNode(StmtNode.StmtType.Printf,printtk,lparent,rparent,semis,format,expNodes,commas);
        }
        else{
            error();
            return null;
        }
    }

    private ForStmtNode ForStmt() {
        //ForStmt → LVal '=' Exp
        LValNode lval = LVal();
        Token assign=match(TokenType.ASSIGN);
        ExpNode expNode =Exp();
        return new ForStmtNode(lval, assign, expNode);
    }
    private ExpNode Exp() {
        //Exp → AddExp
        AddExpNode addExpNode =AddExp();
        return new ExpNode(addExpNode);
    }
    private CondNode Cond() {
        //Cond → LOrExp
        LOrExpNode lorExpNode =LOrExp();
        return new CondNode(lorExpNode);
    }
    private LValNode LVal() {
        //LVal → Ident {'[' Exp ']'}
        Token ident=match(TokenType.IDENFR);
        List<Token> lbracks=new ArrayList<>();
        List<ExpNode> expNodes=new ArrayList<>();
        List<Token> rbracks=new ArrayList<>();
        while(getCurrentToken().getType()==TokenType.LBRACK){
            lbracks.add(match(TokenType.LBRACK));
            expNodes.add(Exp());
            rbracks.add(match(TokenType.RBRACK));
        }
        return new LValNode(ident,lbracks,expNodes,rbracks);
    }
    private PrimaryExpNode PrimaryExp(){
        //PrimaryExp → '(' Exp ')' | LVal | Number
        Token lparent=null;
        ExpNode expNode=null;
        Token rparent=null;
        LValNode lval=null;
        NumberNode nval=null;
        if(getCurrentToken().getType()==TokenType.LPARENT){
            lparent=match(TokenType.LPARENT);
            expNode=Exp();
            rparent=match(TokenType.RPARENT);
        } else if(getCurrentToken().getType()==TokenType.IDENFR){
            lval=LVal();
        }
        else{
            nval=NewNumber();
        }
        return new PrimaryExpNode(lparent,rparent,expNode,lval,nval);
    }

    private NumberNode NewNumber() {
        //Number → IntConst
        Token intconst=match(TokenType.INTCON);
        return new NumberNode(intconst);
    }
    private UnaryExpNode UnaryExp(){
        //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'| UnaryOp UnaryExp
        PrimaryExpNode primaryExpNode=null;
        Token ident=null;
        Token lparent=null;
        FuncRParamsNode fparams=null;
        Token rparent=null;
        UnaryOpNode unaryOp=null;
        UnaryExpNode unaryExp=null;
        if(getCurrentToken().getType()==TokenType.IDENFR&&tokens.get(currentTokenIndex+1).getType()==TokenType.LPARENT){
            ident=match(TokenType.IDENFR);
            lparent=match(TokenType.LPARENT);
            if(getCurrentToken().getType()!=TokenType.RPARENT){
                fparams=FuncRparams();
            }
            rparent=match(TokenType.RPARENT);
        } else if (getCurrentToken().getType()==TokenType.PLUS||getCurrentToken().getType()==TokenType.MINU||getCurrentToken().getType()==TokenType.NOT) {
            unaryOp=UnaryOp();
            unaryExp=UnaryExp();
        } else {
            primaryExpNode=PrimaryExp();
        }
        return new UnaryExpNode(primaryExpNode,ident,lparent,fparams,rparent,unaryOp,unaryExp);
    }

    private UnaryOpNode UnaryOp() {
        //UnaryOp → '+' | '−' | '!'
        Token plus=null;
        Token minus=null;
        Token not=null;
        if (getCurrentToken().getType()==TokenType.PLUS){
            plus=match(TokenType.PLUS);
        } else if (getCurrentToken().getType()==TokenType.MINU) {
            minus=match(TokenType.MINU);
        }
        else{
            not=match(TokenType.NOT);
        }
        return new UnaryOpNode(plus,minus,not);
    }

    private FuncRParamsNode FuncRparams() {
        //FuncRParams → Exp { ',' Exp }
        List<ExpNode> expNodes =new ArrayList<>();
        List<Token> commas=new ArrayList<>();
        expNodes.add(Exp());
        while(getCurrentToken().getType()==TokenType.COMMA) {
            commas.add(match(TokenType.COMMA));
            expNodes.add(Exp());
        }
        return new FuncRParamsNode(commas,expNodes);
    }
    private MulExpNode MulExp(){
        //MulExp → UnaryExp {('*' | '/' | '%') UnaryExp}
        List<UnaryExpNode>unaryExpNodes=new ArrayList<>();
        List<Token>ops=new ArrayList<>();
        unaryExpNodes.add(UnaryExp());
        while(getCurrentToken().getType()==TokenType.MULT||getCurrentToken().getType()==TokenType.DIV||getCurrentToken().getType()==TokenType.MOD){
            if(getCurrentToken().getType()==TokenType.MULT){
                ops.add(match(TokenType.MULT));
            } else if (getCurrentToken().getType()==TokenType.DIV) {
                ops.add(match(TokenType.DIV));
            }
            else{
                ops.add(match(TokenType.MOD));
            }
            unaryExpNodes.add(UnaryExp());
        }
        return new MulExpNode(unaryExpNodes,ops);
    }
    private AddExpNode AddExp(){
        //AddExp → MulExp {('+' | '−') MulExp}
        List<MulExpNode> mulExpNodes=new ArrayList<>();
        List<Token> ops=new ArrayList<>();
        mulExpNodes.add(MulExp());
        while(getCurrentToken().getType()==TokenType.PLUS||getCurrentToken().getType()==TokenType.MINU){
            if(getCurrentToken().getType()==TokenType.PLUS){
                ops.add(match(TokenType.PLUS));
            }
            else{
                ops.add(match(TokenType.MINU));
            }
            mulExpNodes.add(MulExp());
        }
        return new AddExpNode(ops, mulExpNodes);
    }
    private RelExpNode RelExp() {
        //RelExp → AddExp {('<' | '>' | '<=' | '>=') AddExp}
        List<AddExpNode> addExpNodes =new ArrayList<>();
        List<Token>ops=new ArrayList<>();
        addExpNodes.add(AddExp());
        while (getCurrentToken().getType() == TokenType.LSS||getCurrentToken().getType() == TokenType.LEQ||getCurrentToken().getType() == TokenType.GRE||getCurrentToken().getType() == TokenType.GEQ) {
            if(getCurrentToken().getType() == TokenType.LSS){
                ops.add(match(TokenType.LSS));
            } else if (getCurrentToken().getType() == TokenType.LEQ) {
                ops.add(match(TokenType.LEQ));
            } else if (getCurrentToken().getType() == TokenType.GRE) {
                ops.add(match(TokenType.GRE));
            } else if (getCurrentToken().getType() == TokenType.GEQ) {
                ops.add(match(TokenType.GEQ));
            }
            addExpNodes.add(AddExp());
        }
        return new RelExpNode(addExpNodes,ops);
    }

    private EqExpNode EqExp() {
        //EqExp → RelExp {('==' | '!=') RelExp}
        List<RelExpNode>relExpNodes=new ArrayList<>();
        List<Token>eqlOrNeqs=new ArrayList<>();
        relExpNodes.add(RelExp());
        while(getCurrentToken().getType()==TokenType.EQL||getCurrentToken().getType()==TokenType.NEQ){
            if (getCurrentToken().getType()==TokenType.EQL){
                eqlOrNeqs.add(match(TokenType.EQL));
            }
            else {
                eqlOrNeqs.add(match(TokenType.NEQ));
            }
            relExpNodes.add(RelExp());
        }
        return new EqExpNode(relExpNodes, eqlOrNeqs);
    }
    private LAndExpNode LAndExp() {
        //LAndExp → EqExp {'&&' EqExp}
        List<EqExpNode> eqExpNodes=new ArrayList<>();
        List <Token> ands=new ArrayList<>();
        eqExpNodes.add(EqExp());
        while (getCurrentToken().getType()==TokenType.AND){
            ands.add(match(TokenType.AND));
            eqExpNodes.add(EqExp());
        }
        return new LAndExpNode(eqExpNodes,ands);
    }
    private LOrExpNode LOrExp() {
        //LOrExp → LAndExp {'||' LAndExp}
        List<LAndExpNode> landExpNodes=new ArrayList<>();
        List<Token> ors=new ArrayList<>();
        landExpNodes.add(LAndExp());
        while(getCurrentToken().getType()==TokenType.OR){
            ors.add(match(TokenType.OR));
            landExpNodes.add(LAndExp());
        }
        return new LOrExpNode(landExpNodes,ors);
    }

    private ConstExpNode ConstExp() {
        //ConstExp → AddExp
        AddExpNode addExp = AddExp();
        return new ConstExpNode(addExp);
    }





    // FuncDef规则

    // MainFuncDef规则


    // 匹配当前 front.Token 并返回
    private Token match(TokenType type) {
        Token currentToken = getCurrentToken();
        if (currentToken.getType() == type) {
            advance();
            return currentToken;
        }else if (type==TokenType.SEMICN){
            error.addError(error.errorType.i,tokens.get(currentTokenIndex-1).getLineNumber());
            return currentToken;
        } else if (type==TokenType.RPARENT) {
            error.addError(error.errorType.j,tokens.get(currentTokenIndex-1).getLineNumber());
            return currentToken;
        }else if (type == TokenType.LBRACK){
            error.addError(error.errorType.k,tokens.get(currentTokenIndex-1).getLineNumber());
            return currentToken;
        }
        else {
            throw new RuntimeException("Syntax error: Expected " + type + " but found " + currentToken.getType());
        }
    }

    // 获取当前 front.Token
    private Token getCurrentToken() {
        return tokens.get(currentTokenIndex);
    }

    // 是否还有下一个 front.Token
    private boolean hasNextToken() {
        return currentTokenIndex < tokens.size();
    }

    // 前进到下一个 front.Token
    private void advance() {
        currentTokenIndex++;
    }
}