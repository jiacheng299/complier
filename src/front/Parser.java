package front;

import node.*;

import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int currentTokenIndex;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }
    private void error(){
        System.out.println("error");
    }
    // 解析入口方法
    public void parse() {
        compUnit();
    }

    // CompUnit规则
    private CompUnitNode compUnit() {
        // CompUnit -> {Decl} {FuncDef} MainFuncDef
        List<DeclNode>declNodes = null;
        List<FuncDefNode>funcDefNodes=null;
        MainFuncDefNode mainFuncDefNode=null;
        while (hasNextToken()) {
            if (tokens.get(currentTokenIndex+2).getType()!=TokenType.LPARENT&&tokens.get(currentTokenIndex).getType()!=TokenType.INTTK) {
                declNodes.add(decl());
            } else if (tokens.get(currentTokenIndex).getType()!=TokenType.INTTK) {
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
        if(tokens.get(currentTokenIndex).getType()==TokenType.CONSTTK){
            constDeclNode=constDecl();
        } else if (tokens.get(currentTokenIndex).getType()==TokenType.INTTK) {
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
        List<Token> commas=null;
        List<ConstDefNode> constDefNodes=null;
        while(tokens.get(currentTokenIndex).getType()==TokenType.COMMA){
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
        List<Token> lbracks=null;
        List<ConstExpNode> constExpNodes=null;
        List<Token> rbracks=null;
        Token assign=null;
        ConstInitValNode constInitValNode=null;
        while (tokens.get(currentTokenIndex).getType()==TokenType.LBRACK){
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
        List<Token> commas=null;
        List<ConstInitValNode>constInitValNodes=null;
        Token rbrace=null;
        if (tokens.get(currentTokenIndex).getType()==TokenType.LBRACE){
            lbrace=match(TokenType.LBRACE);
            if (tokens.get(currentTokenIndex).getType()==TokenType.RBRACE){
            }
            else{
                constInitValNodes.add(ConstInitVal());
                while(tokens.get(currentTokenIndex).getType()==TokenType.COMMA){
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
        List<Token> commas=null;
        List<VarDefNode> varDefNodes=null;
        Token semi=null;
        while(tokens.get(currentTokenIndex).getType()==TokenType.COMMA){
            commas.add(match(TokenType.COMMA));
            varDefNodes.add(VarDef());
        }
        return new VarDeclNode(bTypeNode,varDefNode,commas,varDefNodes,semi);
    }

    private VarDefNode VarDef() {
    //VarDef → Ident { '[' ConstExp ']' }| Ident { '[' ConstExp ']' } '=' InitVal
        Token ident=match(TokenType.IDENFR);
        List<Token> lbracks=null;
        List<ConstExpNode> constExpNodes=null;
        List<Token> rbracks=null;
        Token assign=null;
        InitValNode intiValue=null;
        while(tokens.get(currentTokenIndex).getType()==TokenType.LBRACK) {
            lbracks.add(match(TokenType.LBRACK));
            constExpNodes.add(ConstExp());
            rbracks.add(match(TokenType.RBRACK));
        }
        assign=match(TokenType.ASSIGN);
        intiValue=InitVal();
        return new VarDefNode(ident,lbracks,constExpNodes,rbracks,assign,intiValue);
    }

    private InitValNode InitVal() {
        //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        ExpNode expNode = null;
        Token lbrace=null;
        List<Token> commas=null;
        List<InitValNode> initValNodes=null;
        Token rbrace=null;
        if(tokens.get(currentTokenIndex).getType()==TokenType.LBRACE){
            lbrace=match(TokenType.LBRACE);
            if(tokens.get(currentTokenIndex).getType()==TokenType.RBRACE){

            }
            else{
                initValNodes.add(InitVal());
                while(tokens.get(currentTokenIndex).getType()==TokenType.COMMA){
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
        if(tokens.get(currentTokenIndex).getType() == TokenType.RPARENT) {
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
        if(tokens.get(currentTokenIndex).getType()!=TokenType.INTTK){
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
        List<Token> commas=null;
        List<FuncFParamNode> funcFParamNodes=null;
        funcFParamNodes.add(FuncFParam());
        while(tokens.get(currentTokenIndex).getType()==TokenType.COMMA){
            commas.add(match(TokenType.COMMA));
            funcFParamNodes.add(FuncFParam());
        }
        return new FuncFParamsNode(commas, funcFParamNodes);
    }

    private FuncFParamNode FuncFParam() {
        // FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        BTypeNode btypeNode=Btype();
        Token ident=match(TokenType.IDENFR);
        List<Token> lbracks=null;
        List<Token> rbracks=null;
        List<ConstExpNode>constExpNodes=null;
        if (tokens.get(currentTokenIndex).getType()==TokenType.LBRACK){
            lbracks.add(match(TokenType.LBRACK));
            rbracks.add(match(TokenType.RBRACK));
            while(tokens.get(currentTokenIndex).getType()==TokenType.LBRACK){
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
        List<BlockItemNode> blockItemNodes=null;
        while (tokens.get(currentTokenIndex).getType()!=TokenType.RBRACE){
            blockItemNodes.add(BlockItem());
        }
        Token rbrace=match(TokenType.RBRACE);
        return new BlockNode(lbrace,blockItemNodes,rbrace);
    }

    private BlockItemNode BlockItem() {
        //BlockItem → Decl | Stmt
        DeclNode declNode=null;
        StmtNode st=null;
        if(tokens.get(currentTokenIndex).getType()==TokenType.CONSTTK||tokens.get(currentTokenIndex).getType()==TokenType.INTTK){
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
        List<Token> commas=null;
        List<ExpNode> expNodes=null;
        List<Token> semis=null;
        List<StmtNode> stmtNodes = null;
        List<ForStmtNode> forStmtNodes=null;
        //Lval
        if (tokens.get(currentTokenIndex).getType()==TokenType.IDENFR){
            lvalNode=LVal();
            assign=match(TokenType.ASSIGN);
            //| LVal '=' 'getint''('')'';'
            if (tokens.get(currentTokenIndex).getType()==TokenType.GETINTTK){
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
        } else if (tokens.get(currentTokenIndex).getType()==TokenType.SEMICN||tokens.get(currentTokenIndex).getType()==TokenType.IDENFR||tokens.get(currentTokenIndex).getType() == TokenType.LPARENT||tokens.get(currentTokenIndex).getType()==TokenType.INTCON) {
            //| [Exp] ';'
            if(tokens.get(currentTokenIndex).getType() == TokenType.SEMICN){
                semis.add(match(TokenType.SEMICN));
            }
            else{
                expNode=Exp();
            }
            return new StmtNode(StmtNode.StmtType.Exp,expNode,semis);
        }else if (tokens.get(currentTokenIndex).getType()==TokenType.LBRACE) {
            //| Block
            blockNode=Block();
            return new StmtNode(StmtNode.StmtType.Block,blockNode);
        }else if (tokens.get(currentTokenIndex).getType()==TokenType.IFTK) {
            //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            iftk=match(TokenType.IFTK);
            lparent=match(TokenType.LPARENT);
            condNode=Cond();
            rparent=match(TokenType.RPARENT);
            stmtNodes.add(Stmt());
            if (tokens.get(currentTokenIndex).getType()==TokenType.ELSETK){
                elsetk=match(TokenType.ELSETK);
                stmtNodes.add(Stmt());
            }
            return new StmtNode(StmtNode.StmtType.If,iftk,lparent,condNode,rparent,stmtNodes,elsetk);
        }else if (tokens.get(currentTokenIndex).getType()==TokenType.FORTK) {
            //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
            fortk=match(TokenType.FORTK);
            lparent=match(TokenType.LPARENT);
            if (tokens.get(currentTokenIndex).getType()!=TokenType.SEMICN){
                forStmtNodes.add(ForStmt());
            }
            semis.add(match(TokenType.SEMICN));
            if (tokens.get(currentTokenIndex).getType()!=TokenType.SEMICN){
                condNode=Cond();
            }
            semis.add(match(TokenType.SEMICN));
            if (tokens.get(currentTokenIndex).getType()!=TokenType.RPARENT){
                forStmtNodes.add(ForStmt());
            }
            rparent=match(TokenType.RPARENT);
            stmtNodes.add(Stmt());
            return new StmtNode(StmtNode.StmtType.For,fortk,lparent,forStmtNodes,semis,condNode,rparent,stmtNodes);
        }else if (tokens.get(currentTokenIndex).getType()==TokenType.BREAKTK||tokens.get(currentTokenIndex).getType()==TokenType.CONTINUETK) {
            //| 'break' ';' | 'continue' ';'
            if(tokens.get(currentTokenIndex).getType()==TokenType.BREAKTK){
                breakOrContinuetk=match(TokenType.BREAKTK);
                semis.add(match(TokenType.SEMICN));
                return new StmtNode(StmtNode.StmtType.Break,breakOrContinuetk,semis);
            }
            else{
                breakOrContinuetk=match(TokenType.CONTINUETK);
                semis.add(match(TokenType.SEMICN));
                return new StmtNode(StmtNode.StmtType.Continue,breakOrContinuetk,semis);
            }
        }else if (tokens.get(currentTokenIndex).getType()==TokenType.RETURNTK) {
            //| 'return' [Exp] ';'
            returntk=match(TokenType.RETURNTK);
            if(tokens.get(currentTokenIndex).getType()!=TokenType.SEMICN){
                expNode=Exp();
            }
            semis.add(match(TokenType.SEMICN));
            return new StmtNode(StmtNode.StmtType.Return,returntk,expNode,semis);
        }else if (tokens.get(currentTokenIndex).getType()==TokenType.PRINTFTK) {
            //| 'printf''('FormatString{','Exp}')'';'
            printtk=match(TokenType.PRINTFTK);
            lparent=match(TokenType.LPARENT);
            format=match(TokenType.STRCON);
            while(tokens.get(currentTokenIndex).getType()==TokenType.COMMA){
                commas.add(match(TokenType.COMMA));
                expNodes.add(Exp());
            }
            rparent=match(TokenType.RPARENT);
            semis.add(match(TokenType.SEMICN));
            return new StmtNode(StmtNode.StmtType.Printf,printtk,lparent,rparent,semis,format,expNodes);
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
        List<Token> lbracks=null;
        List<ExpNode> expNodes=null;
        List<Token> rbracks=null;
        while(tokens.get(currentTokenIndex).getType()==TokenType.LBRACK){
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
        if(tokens.get(currentTokenIndex).getType()==TokenType.LPARENT){
            lparent=match(TokenType.LPARENT);
            expNode=Exp();
            rparent=match(TokenType.RPARENT);
        } else if(tokens.get(currentTokenIndex).getType()==TokenType.IDENFR){
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
        if(tokens.get(currentTokenIndex).getType()==TokenType.IDENFR){
            ident=match(TokenType.IDENFR);
            lparent=match(TokenType.LPARENT);
            if(tokens.get(currentTokenIndex).getType()!=TokenType.RPARENT){
                fparams=FuncRparams();
            }
            rparent=match(TokenType.RPARENT);
        } else if (tokens.get(currentTokenIndex).getType()==TokenType.PLUS||tokens.get(currentTokenIndex).getType()==TokenType.MINU||tokens.get(currentTokenIndex).getType()==TokenType.NOT) {
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
        if (tokens.get(currentTokenIndex).getType()==TokenType.PLUS){
            plus=match(TokenType.PLUS);
        } else if (tokens.get(currentTokenIndex).getType()==TokenType.MINU) {
            minus=match(TokenType.MINU);
        }
        else{
            not=match(TokenType.NOT);
        }
        return new UnaryOpNode(plus,minus,not);
    }

    private FuncRParamsNode FuncRparams() {
        //FuncRParams → Exp { ',' Exp }
        List<ExpNode> expNodes =null;
        List<Token> commas=null;
        expNodes.add(Exp());
        while(tokens.get(currentTokenIndex).getType()==TokenType.COMMA) {
            commas.add(match(TokenType.COMMA));
            expNodes.add(Exp());
        }
        return new FuncRParamsNode(commas,expNodes);
    }
    private MulExpNode MulExp(){
        //MulExp → UnaryExp {('*' | '/' | '%') UnaryExp}
        List<UnaryExpNode>unaryExpNodes=null;
        List<Token>ops=null;
        unaryExpNodes.add(UnaryExp());
        while(tokens.get(currentTokenIndex).getType()==TokenType.MULT||tokens.get(currentTokenIndex).getType()==TokenType.DIV||tokens.get(currentTokenIndex).getType()==TokenType.MOD){
            if(tokens.get(currentTokenIndex).getType()==TokenType.MULT){
                ops.add(match(TokenType.MULT));
            } else if (tokens.get(currentTokenIndex).getType()==TokenType.DIV) {
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
        List<MulExpNode> mulExpNodes=null;
        List<Token> ops=null;
        mulExpNodes.add(MulExp());
        while(tokens.get(currentTokenIndex).getType()==TokenType.PLUS||tokens.get(currentTokenIndex).getType()==TokenType.MINU){
            if(tokens.get(currentTokenIndex).getType()==TokenType.PLUS){
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
        List<AddExpNode> addExpNodes =null;
        List<Token>ops=null;
        addExpNodes.add(AddExp());
        while (tokens.get(currentTokenIndex).getType() == TokenType.LSS||tokens.get(currentTokenIndex).getType() == TokenType.LEQ||tokens.get(currentTokenIndex).getType() == TokenType.GRE||tokens.get(currentTokenIndex).getType() == TokenType.GEQ) {
            if(tokens.get(currentTokenIndex).getType() == TokenType.LSS){
                ops.add(match(TokenType.LSS));
            } else if (tokens.get(currentTokenIndex).getType() == TokenType.LEQ) {
                ops.add(match(TokenType.LEQ));
            } else if (tokens.get(currentTokenIndex).getType() == TokenType.GRE) {
                ops.add(match(TokenType.GRE));
            } else if (tokens.get(currentTokenIndex).getType() == TokenType.GEQ) {
                ops.add(match(TokenType.GEQ));
            }
            addExpNodes.add(AddExp());
        }
        return new RelExpNode(addExpNodes,ops);
    }

    private EqExpNode EqExp() {
        //EqExp → RelExp {('==' | '!=') RelExp}
        List<RelExpNode>relExpNodes=null;
        List<Token>eqlOrNeqs=null;
        relExpNodes.add(RelExp());
        while(tokens.get(currentTokenIndex).getType()==TokenType.EQL||tokens.get(currentTokenIndex).getType()==TokenType.NEQ){
            if (tokens.get(currentTokenIndex).getType()==TokenType.EQL){
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
        List<EqExpNode> eqExpNodes=null;
        List <Token> ands=null;
        eqExpNodes.add(EqExp());
        while (tokens.get(currentTokenIndex).getType()==TokenType.AND){
            ands.add(match(TokenType.AND));
            eqExpNodes.add(EqExp());
        }
        return new LAndExpNode(eqExpNodes,ands);
    }
    private LOrExpNode LOrExp() {
        //LOrExp → LAndExp {'||' LAndExp}
        List<LAndExpNode> landExpNodes=null;
        List<Token> ors=null;
        landExpNodes.add(LAndExp());
        while(tokens.get(currentTokenIndex).getType()==TokenType.OR){
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
        } else {
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