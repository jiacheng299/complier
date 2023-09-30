package Token;

public enum TokenType {
    //保留字
    IDENFR,
    //数字
    INTCON,
    //格式字符串终结符
    STRCON,
    //程序入口
    MAINTK,
    //常量
    CONSTTK,
    INTTK,
    BREAKTK,
    CONTINUETK,
    IFTK,
    ELSETK,
    NOT,
    AND,
    OR,
    FORTK,
    GETINTTK,
    PRINTFTK,
    RETURNTK,
    //操作符
    PLUS,
    MINU,
    VOIDTK,
    MULT,
    DIV,
    MOD,
    LSS,
    LEQ,
    GRE,
    GEQ,
    EQL,
    NEQ,
    //分号
    SEMICN,
    //逗号
    COMMA,

    //左扩号
    LPARENT,
    //右扩号
    RPARENT,
    //斜线
    DIVISY,
    ASSIGN,
    QUOTE,
    LBRACK,
    RBRACK,
    LBRACE,
    RBRACE,

}
