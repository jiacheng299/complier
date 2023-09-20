package front;

import java.util.ArrayList;
import java.util.List;

public class Token {
    private TokenType type;
    private String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type + " " + value ;
    }
}
class Constants {
    public static final List<String> Keyword;

    static {
        // 初始化字符串列表
        Keyword = new ArrayList<>();
        Keyword.add("main");
        Keyword.add("const");
        Keyword.add("if");
        Keyword.add("int");
        Keyword.add("break");
        Keyword.add("continue");
        Keyword.add("else");
        Keyword.add("for");
        Keyword.add("getint");
        Keyword.add("printf");
        Keyword.add("return");
        Keyword.add("void");
    }
}
enum TokenType {
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

public class Lexer {
    private String input;
    private int position;
    private int flag=0;
    private int lineNumber=1;
    public Lexer(String input) {
        this.input = input;
        this.position = 0;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            char currentChar = input.charAt(position);
            //如果读到的是空字符
            if (Character.isWhitespace(currentChar)) {
                if(currentChar=='\n') lineNumber+=1;
                position++;
                continue;
            }
            //如果读到单行注释
            if(isDivi(currentChar)){
                if(isDivi(input.charAt(position+1))){
                    position+=2;
                    while(input.charAt(position)!='\n'){
                        position++;
                    }
                    continue;
                }
            }
            /*
               如果读到多行注释
             */
            if(isDivi(currentChar)){
                if(input.charAt(position+1)=='*'){
                    position+=2;
                    while(input.charAt(position)!='*'||input.charAt(position+1)!='/'){
                        position++;
                    }
                    position+=2;
                    continue;
                }
            }
            if(isFormatString(currentChar)){

                tokens.add(scanFormatString());
                continue;
            }
            //如果读到的是字母或下划线
            if (Character.isLetter(currentChar)||currentChar=='_') {
                tokens.add(scanString());
                continue;
            }
            //如果读到的是数字
            if (Character.isDigit(currentChar)) {
                tokens.add(scanNumber());
                continue;
            }

            //读到&&
            if(isAnd(currentChar,input.charAt(position+1))){
                tokens.add(scanAnd());
                continue;
            }
            //读到||
            if(isOr(currentChar,input.charAt(position+1))){
                tokens.add(scanOr());
                continue;
            }
            //读到+
            if(isPlus(currentChar)){
                tokens.add(scanPlus());
                continue;
            }
            //读到-
            if(isMinu(currentChar)){
                tokens.add(scanMinu());
                continue;
            }
            //读到*
            if(isMult(currentChar)){
                tokens.add(scanMult());
                continue;
            }
            //读到/
            if(isDivi(currentChar)){
                tokens.add(scanDiv());
                continue;
            }
            //读到%
            if(isMod(currentChar)){
                tokens.add(scanMod());
                continue;
            }

            //读到<=
            if(isLeq(currentChar,input.charAt(position+1))){
                tokens.add(scanLeq());
                continue;
            }
            //读到<
            if(isLss(currentChar)){
                tokens.add(scanLss());
                continue;
            }
            //读到>=
            if(isGeq(currentChar,input.charAt(position+1))){
                tokens.add(scanGeq());
                continue;
            }
            //读到>
            if(isGre(currentChar)){
                tokens.add(scanGre());
                continue;
            }
            //读到==
            if(isEql(currentChar,input.charAt(position+1))){
                tokens.add(scanEql());
                continue;
            }
            //读到!=
            if(isNeq(currentChar,input.charAt(position+1))){
                tokens.add(scanNeq());
                continue;
            }
            //读到!
            if(isNot(currentChar)){
                tokens.add(scanNot());
                continue;
            }
            //读到[
            if(isLbrack(currentChar)){
                tokens.add(scanLbrack());
                continue;
            }
            //读到]
            if(isRbrack(currentChar)){
                tokens.add(scanRbrack());
                continue;
            }
            //读到{
            if(isLbrace(currentChar)){
                tokens.add(scanLbrace());
                continue;
            }
            //读到}
            if(isRbrace(currentChar)){
                tokens.add(scanRbrace());
                continue;
            }

            //如果读到的是左括号
            if(isLpar(currentChar)){
                if(flag==1){
                    flag=2;
                }
                tokens.add(scanLpar());
                continue;
            }
            //如果读到的是右括号
            if(isRpar(currentChar)){
                tokens.add(scanRpar());
                continue;
            }
            //如果读到的是分号
            if(isSemi(currentChar)){
                tokens.add(scanSemi());
                continue;
            }

            //如果读到的是逗号
            if(isComma(currentChar)){
                tokens.add(scanComma());
                continue;
            }
            //如果读到的是等号
            if(isEqu(currentChar)){
                tokens.add(scanAssign());
                continue;
            }

            position++;
        }

        return tokens;
    }
    //判断是否是FormatString
    private boolean isFormatString(char ch){
      if(flag==2){
          if(ch!='"'){
              flag=0;
              return false;
          }
          else return true;
      }
      else return false;
    };
    //判断是否是！
    private boolean isNot(char ch){return ch=='!';}
    //判断是否是&&
    private boolean isAnd(char ch1,char ch2){return ch1=='&'&&ch2=='&';}
    //判断是否是||
    private boolean isOr(char ch1,char ch2){return ch1=='|'&&ch2=='|';}
    //判断是否是+
    private boolean isPlus(char ch){return ch=='+';}
    //判断是否是-
    private boolean isMinu(char ch){return ch=='-';}
    //判断是否是*
    private boolean isMult(char ch){return ch=='*';}
    //判断是否是%
    private boolean isMod(char ch){return ch=='%';}
    //判断是否是<
    private boolean isLss(char ch){return ch=='<';}
    //判断是否是<=
    private boolean isLeq(char ch1,char ch2){return ch1=='<' && ch2=='=';}
    //判断是否是>
    private boolean isGre(char ch){return ch=='>';}
    //判断是否是>=
    private boolean isGeq(char ch1,char ch2){return ch1=='>' && ch2=='=';}
    //判断是否是==
    private boolean isEql(char ch1,char ch2){return ch1=='=' && ch2=='=';}
    //判断是否是!=
    private boolean isNeq(char ch1,char ch2){return ch1=='!' && ch2=='=';}
    //判断是否是[
    private boolean isLbrack(char ch){return ch=='[';}
    //判断是否是]
    private boolean isRbrack(char ch){return ch==']';}
    //判断是否是{
    private boolean isLbrace(char ch){return ch=='{';}
    //判断是否是}
    private boolean isRbrace(char ch){return ch=='}';}
    //判断是否是/
    private boolean isDivi(char ch){return ch=='/';}
    //判断是否是;
    private boolean isSemi(char ch){return ch==';';}
    //判断是否是=
    private boolean isEqu(char ch){return ch=='=';}
    //判断是否是,
    private boolean isComma(char ch){return ch==',';}

    //判断是否是(
    private boolean isLpar(char ch){return ch=='(';}
    //判断是否是)
    private boolean isRpar(char ch){return ch==')';}

    //读取!
    private Token scanNot(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.NOT,String.valueOf(temp));
    }
    //读取&&
    private Token scanAnd(){
        String temp="&&";
        position+=2;
        return new Token(TokenType.AND,temp);
    }
    //读取||
    private Token scanOr(){
        String temp="||";
        position+=2;
        return new Token(TokenType.OR,temp);
    }
    //读取+
    private Token scanPlus(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.PLUS,String.valueOf(temp));
    }
    //读取-
    private Token scanMinu(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.MINU,String.valueOf(temp));
    }
    //读取*
    private Token scanMult(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.MULT,String.valueOf(temp));
    }
    //读取/
    private Token scanDiv(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.DIV,String.valueOf(temp));
    }
    //读取%
    private Token scanMod(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.MOD,String.valueOf(temp));
    }
    //读取<
    private Token scanLss(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.LSS,String.valueOf(temp));
    }
    //读取<=
    private Token scanLeq(){
        String temp="<=";
        position+=2;
        return new Token(TokenType.LEQ,temp);
    }
    //读取>
    private Token scanGre(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.GRE,String.valueOf(temp));
    }
    //读取>=
    private Token scanGeq(){
        String temp=">=";
        position+=2;
        return new Token(TokenType.GEQ,temp);
    }
    //读取==
    private Token scanEql(){
        String temp="==";
        position+=2;
        return new Token(TokenType.EQL,temp);
    }
    //读取!=
    private Token scanNeq(){
        String temp="!=";
        position+=2;
        return new Token(TokenType.NEQ,temp);
    }
    //读取=
    private Token scanAssign(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.ASSIGN,String.valueOf(temp));
    }
    //读;
    private Token scanSemi(){
        char semi=input.charAt(position);
        position++;
        return new Token(TokenType.SEMICN,String.valueOf(semi));
    }
    //读,
    private Token scanComma(){
        char comma=input.charAt(position);
        position++;
        return new Token(TokenType.COMMA,String.valueOf(comma));
    }
    //读(
    private Token scanLpar(){
        char parens=input.charAt(position);
        position++;
        return new Token(TokenType.LPARENT,String.valueOf(parens));
    }
    //读)
    private Token scanRpar(){
        char parens=input.charAt(position);
        position++;
        return new Token(TokenType.RPARENT,String.valueOf(parens));
    }
    //读取[
    private Token scanLbrack(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.LBRACK,String.valueOf(temp));
    }
    //读取]
    private Token scanRbrack(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.RBRACK,String.valueOf(temp));
    }
    //读取{
    private Token scanLbrace(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.LBRACE,String.valueOf(temp));
    }
    //读取}
    private Token scanRbrace(){
        char temp=input.charAt(position);
        position++;
        return new Token(TokenType.RBRACE,String.valueOf(temp));
    }
    private Token scanFormatString(){
        StringBuilder formatString=new StringBuilder();
        formatString.append(input.charAt(position));
        position++;
        char currentChar=input.charAt(position);
        while(currentChar!='"'){
            formatString.append(currentChar);
            position++;
            if(position>=input.length()){
                break;
            }
            currentChar=input.charAt(position);
        }
        formatString.append(currentChar);
        String temp=formatString.toString();
        flag=0;
        return new Token(TokenType.STRCON,temp);
    }
    //读字符串
    private Token scanString() {
        StringBuilder identifier = new StringBuilder();
        char currentChar = input.charAt(position);
        //一直向标识符中加入字母或数字直到遇到非字母数字
        while (Character.isLetterOrDigit(currentChar)||currentChar=='_') {
            identifier.append(currentChar);
            position++;
            //如果这句话已经读完则直接退出
            if (position >= input.length()) {
                break;
            }
            currentChar = input.charAt(position);
        }
        String temp=identifier.toString();
        //如果是保留字
        if(Constants.Keyword.contains(temp)){
            if(temp.equals("main")){
                return new Token(TokenType.MAINTK, temp);
            } else if (temp.equals("const")) {
                return new Token(TokenType.CONSTTK, temp);
            } else if (temp.equals("int")) {
                return new Token(TokenType.INTTK, temp);
            } else if (temp.equals("break")) {
                return new Token(TokenType.BREAKTK, temp);
            } else if (temp.equals("continue")) {
                return new Token(TokenType.CONTINUETK, temp);
            } else if (temp.equals("if")) {
                return new Token(TokenType.IFTK, temp);
            } else if (temp.equals("else")) {
                return new Token(TokenType.ELSETK, temp);
            } else if (temp.equals("for")) {
                return new Token(TokenType.FORTK, temp);
            } else if (temp.equals("getint")) {
                return new Token(TokenType.GETINTTK, temp);
            } else if (temp.equals("printf")) {
                flag=1;
                return new Token(TokenType.PRINTFTK, temp);
            } else if (temp.equals("return")) {
                return new Token(TokenType.RETURNTK, temp);
            } else if (temp.equals("void")) {
                return new Token(TokenType.VOIDTK, temp);
            }
            return null;
        }
        //否则就是标识符
        else{
            return new Token(TokenType.IDENFR, temp);
        }
    }
    //读数字串
    private Token scanNumber() {
        StringBuilder number = new StringBuilder();
        char currentChar = input.charAt(position);

        while (Character.isDigit(currentChar)) {
            number.append(currentChar);
            position++;
            if (position >= input.length()) {
                break;
            }
            currentChar = input.charAt(position);
        }

        return new Token(TokenType.INTCON, number.toString());
    }

}

