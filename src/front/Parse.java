package front;

import node.NodeType;

import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int currentTokenIndex;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    // 解析入口方法
    public void parse() {
        compUnit();
    }

    // CompUnit规则
    private void compUnit() {
        while (hasNextToken()) {
            if (tokens.get(currentTokenIndex+2).getType()!=TokenType.LPARENT&&tokens.get(currentTokenIndex+1).getType()!=TokenType.MAINTK) {
                decl();
            } else if () {
                funcDef();
            } else {
                break;
            }
        }
        mainFuncDef();
    }

    // Decl规则
    private void decl() {
        // 处理声明语句
        // ...
    }

    // FuncDef规则
    private void funcDef() {
        // 处理函数定义语句
        // ...
    }

    // MainFuncDef规则
    private void mainFuncDef() {
        match(TokenType.MAINFUNC);
        // 处理主函数定义语句
        // ...
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