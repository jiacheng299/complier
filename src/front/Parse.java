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
    }

    private ConstExpNode ConstExp() {
    }





    // FuncDef规则
    private FuncDefNode funcDef() {
        // 处理函数定义语句
        // ...
        return null;
    }

    // MainFuncDef规则
    private MainFuncDefNode mainFuncDef() {
        match(TokenType.MAINTK);
        // 处理主函数定义语句
        // ...
        return null;
    }

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