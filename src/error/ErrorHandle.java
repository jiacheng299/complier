package error;

import Token.TokenType;
import node.*;
import symbol.SymbolInfo;
import symbol.SymbolTableNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ErrorHandle {
    private static final ErrorHandle instance=new ErrorHandle();
    private SymbolTableNode currentNode=SymbolTableNode.getCurrentNode();
    public ErrorHandle getInstance(){return instance;}
    public void start(CompUnitNode compUnitNode){
        //CompUnit → {Decl} {FuncDef} MainFuncDef

        for (DeclNode declNode:compUnitNode.getDeclNodes()){
            handleDecl(declNode);
        }
        for (FuncDefNode funcDef : compUnitNode.getFuncDefNodes()) {
            handleFuncDef(funcDef);
        }
        handleMainFuncDef(compUnitNode.getMainFuncDefNode());
    }
    private SymbolInfo.SymbolType getFuncRParamInExp(ExpNode expNode){
        UnaryExpNode unaryExpNode=expNode.getAddExpNode().getMulExpNodes().get(0).getUnaryExpNodes().get(0);
        return getFuncRParamInUnaryExp(unaryExpNode);
    }
    private SymbolInfo.ReturnType getReturnType(){
        List<SymbolInfo> reversedList = new ArrayList<>(currentNode.getParent().getSymbolMap().values());
        Collections.reverse(reversedList);
        for (SymbolInfo symbolInfo : reversedList) {
            if (symbolInfo.getType()==SymbolInfo.SymbolType.func){
                return symbolInfo.getReturnType();
            }
        }
        return null;
    }
    private SymbolInfo.SymbolType getFuncRParamInUnaryExp(UnaryExpNode unaryExpNode){
        if (unaryExpNode.getPrimaryExpNode()!=null){
            if (unaryExpNode.getPrimaryExpNode().getlValNode()!=null){
                SymbolInfo symbolInfo=currentNode.getSymbol(unaryExpNode.getPrimaryExpNode().getlValNode().getIdent().getValue());
                if (symbolInfo.getType()== SymbolInfo.SymbolType.func){
                    if (symbolInfo.getReturnType()==SymbolInfo.ReturnType.VOID) return SymbolInfo.SymbolType.VOID;
                    else return SymbolInfo.SymbolType.interger;
                }
                else{
                    return symbolInfo.getType();
                }
            }
            else if (unaryExpNode.getPrimaryExpNode().getNumberNode()!=null){
                return SymbolInfo.SymbolType.interger;
            }
            else{
                return  getFuncRParamInExp(unaryExpNode.getPrimaryExpNode().getExpNode());
            }
        } else if (unaryExpNode.getIdent()!=null) {
            SymbolInfo symbolInfo=currentNode.getSymbol(unaryExpNode.getIdent().getValue());
            if (symbolInfo.getReturnType()==SymbolInfo.ReturnType.VOID) return SymbolInfo.SymbolType.VOID;
            else return SymbolInfo.SymbolType.interger;
        }else {
            return getFuncRParamInUnaryExp(unaryExpNode.getUnaryExpNode());
        }
    }
    private void handleDecl(DeclNode declNode) {
        //Decl → ConstDecl | VarDecl 
        ConstDeclNode constDeclNode=declNode.getConstDeclNode();
        VarDeclNode varDeclNode=declNode.getVarDeclNode();
        if (constDeclNode!=null){
            handleConstDecl(constDeclNode);
        }
        else{
            handleVarDecl(varDeclNode);
        }
    }
    private void handleConstDecl(ConstDeclNode constDeclNode) {
        // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        for (ConstDefNode constDef : constDeclNode.getConstDefNodes()) {
            handleConstDef(constDef);
        }
    }

    private void handleConstDef(ConstDefNode constDefNode) {
        //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        //如果当前作用域下已有过此变量的定义,需要添加到错误信息中
        if (SymbolTableNode.getCurrentNode().getSymbolInCurrent(constDefNode.getIdent().getValue())!=null){
            error.addError(error.errorType.b,constDefNode.getIdent().getLineNumber());
            return;
        }
        SymbolInfo symbolInfo;
        if (constDefNode.getConstExpNodes().size()==0)  symbolInfo=new SymbolInfo(constDefNode.getIdent().getValue(), SymbolInfo.SymbolType.interger,currentNode,true);
        else if (constDefNode.getConstExpNodes().size()==1) {
            symbolInfo=new SymbolInfo(constDefNode.getIdent().getValue(), SymbolInfo.SymbolType.oneArray,currentNode,true);
        }
        else  symbolInfo=new SymbolInfo(constDefNode.getIdent().getValue(), SymbolInfo.SymbolType.twoArray,currentNode,true);
        currentNode.addSymbol(constDefNode.getIdent().getValue(),symbolInfo);
        for (ConstExpNode constExpNode:constDefNode.getConstExpNodes()){
            handleConstExp(constExpNode);
        }

        handleConstInitVal(constDefNode.getConstInitValNode());
    }

    private void handleConstInitVal(ConstInitValNode constInitValNode) {
        //ConstInitVal → ConstExp
        //| '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        ConstExpNode constExpNode=constInitValNode.getConstExpNode();
        if (constExpNode!=null){
            handleConstExp(constExpNode);
        }
        else{
            for (ConstInitValNode constInitValNode1:constInitValNode.getConstInitValNodes()){
                handleConstInitVal(constInitValNode1);
            }
        }
    }
    private void handleVarDecl(VarDeclNode varDeclNode) {
        //VarDecl → BType VarDef { ',' VarDef } ';'
        handleVarDef(varDeclNode.getVarDefNode());
        for (VarDefNode varDefNode:varDeclNode.getVarDefNodes()){
            handleVarDef(varDefNode);
        }
    }

    private void handleVarDef(VarDefNode varDefNode) {
        // VarDef → Ident { '[' ConstExp ']' } // 包含普通变量、一维数组、二维数组定义
        //| Ident { '[' ConstExp ']' } '=' InitVal
        if (SymbolTableNode.getCurrentNode().getSymbolInCurrent(varDefNode.getIdent().getValue())!=null){
            error.addError(error.errorType.b,varDefNode.getIdent().getLineNumber());
            return;
        }
        SymbolInfo symbolInfo;
        if (varDefNode.getConstExpNodes().size()==0)  symbolInfo=new SymbolInfo(varDefNode.getIdent().getValue(), SymbolInfo.SymbolType.interger,currentNode,false);
        else if (varDefNode.getConstExpNodes().size()==1) {
            symbolInfo=new SymbolInfo(varDefNode.getIdent().getValue(), SymbolInfo.SymbolType.oneArray,currentNode,false);
        }
        else  symbolInfo=new SymbolInfo(varDefNode.getIdent().getValue(), SymbolInfo.SymbolType.twoArray,currentNode,false);
        currentNode.addSymbol(varDefNode.getIdent().getValue(),symbolInfo);
        for (ConstExpNode constExpNode:varDefNode.getConstExpNodes()){
            handleConstExp(constExpNode);
        }
        if (varDefNode.getInitValNode()!=null) handleInitVal(varDefNode.getInitValNode());
    }

    private void handleInitVal(InitValNode initValNode) {
        // InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        ExpNode expNode=initValNode.getExpNode();
        if (expNode!=null){
            handleExp(expNode);
        }
        else{
            for (InitValNode initValNode1:initValNode.getInitValNodes()){
                handleInitVal(initValNode1);
            }
        }
    }
    private void handleFuncDef(FuncDefNode funcDef) {
        //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        if (SymbolTableNode.getCurrentNode().getSymbolInCurrent(funcDef.getIdent().getValue())!=null){
            error.addError(error.errorType.b,funcDef.getIdent().getLineNumber());
            return;
        }
        SymbolInfo symbolInfo;
        if (funcDef.getFuncTypeNode().getInttk()!=null)
        symbolInfo=new SymbolInfo(funcDef.getIdent().getValue(),SymbolInfo.SymbolType.func,currentNode,SymbolInfo.ReturnType.INT);
        else
            symbolInfo=new SymbolInfo(funcDef.getIdent().getValue(),SymbolInfo.SymbolType.func,currentNode,SymbolInfo.ReturnType.VOID);

        if (funcDef.getFuncFParamsNode()!=null){
            for (FuncFParamNode funcFParamNode:funcDef.getFuncFParamsNode().getFuncFParamsNodes()){
                SymbolInfo symbolInfo1;
                if (funcFParamNode.getLbracks().size()==0) symbolInfo1=new SymbolInfo(funcFParamNode.getIdent().getValue(), SymbolInfo.SymbolType.interger,currentNode,false);
                else if (funcFParamNode.getLbracks().size()==1)     symbolInfo1=new SymbolInfo(funcFParamNode.getIdent().getValue(), SymbolInfo.SymbolType.oneArray,currentNode,false);
                else symbolInfo1=new SymbolInfo(funcFParamNode.getIdent().getValue(), SymbolInfo.SymbolType.twoArray,currentNode,false);
                symbolInfo.addFuncParam(symbolInfo1);
            }
            handleFuncFParams(funcDef.getFuncFParamsNode());
        }
        currentNode.addSymbol(funcDef.getIdent().getValue(),symbolInfo);
        currentNode.enterScope();
        handleBlock(funcDef.getBlockNode());
    }
    private void handleMainFuncDef(MainFuncDefNode mainFuncDefNode) {
        // MainFuncDef → 'int' 'main' '(' ')' Block
        currentNode.enterScope();
        handleBlock(mainFuncDefNode.getBlockNode());
    }
    private void handleFuncFParams(FuncFParamsNode funcFParamsNode) {
        //FuncFParams → FuncFParam { ',' FuncFParam }
        for (FuncFParamNode funcFParamNode:funcFParamsNode.getFuncFParamsNodes()){
            handleFuncFParam(funcFParamNode);
        }
    }

    private void handleFuncFParam(FuncFParamNode funcFParamNode) {
        //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        if (currentNode.getSymbolInCurrent(funcFParamNode.getIdent().getValue())!=null){
            error.addError(error.errorType.b,funcFParamNode.getIdent().getLineNumber());
            return;
        }
        for (ConstExpNode constExpNode:funcFParamNode.getConstExpNodes()){
            handleConstExp(constExpNode);
        }
    }

    private void handleBlock(BlockNode blockNode) {
        //Block → '{' { BlockItem } '}'
        for (BlockItemNode blockItemNode:blockNode.getBlockItemNodes()){
            handleBlockItem(blockItemNode);
        }

        SymbolInfo.ReturnType temp=getReturnType();
        if (temp!= SymbolInfo.ReturnType.VOID){
            if (blockNode.getBlockItemNodes().size()==0||blockNode.getBlockItemNodes().get(blockNode.getBlockItemNodes().size()-1).getStmtnode()==null||blockNode.getBlockItemNodes().get(blockNode.getBlockItemNodes().size()-1).getStmtnode().getReturntk()==null){
                error.addError(error.errorType.g,blockNode.getRbrace().getLineNumber());
            }
        }
        currentNode.exitScope();
    }

    private void handleBlockItem(BlockItemNode blockItemNode) {
        //BlockItem → Decl | Stmt
        if (blockItemNode.getDeclnode()!=null){
            handleDecl(blockItemNode.getDeclnode());
        }
        else{
            handleStmt(blockItemNode.getStmtnode());
        }
    }

    private void handleStmt(StmtNode stmtnode) {
        // Stmt → LVal '=' Exp ';'
        if (stmtnode.getLvalnode()!=null&&stmtnode.getExpNode()!=null){
            handleLVal(stmtnode.getLvalnode());
            handleExp(stmtnode.getExpNode());
            if(currentNode.getSymbol(stmtnode.getLvalnode().getIdent().getValue()).getConst()==true){
                error.addError(error.errorType.h,stmtnode.getLvalnode().getIdent().getLineNumber());
            }
        }
        //| Block
        else if (stmtnode.getBlockNode()!=null) {
            currentNode.enterScope();
            handleBlock(stmtnode.getBlockNode());
        }
        //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
        else if (stmtnode.getIftk()!=null) {
            handleCond(stmtnode.getCondNode());
            handleStmt(stmtnode.getStmtNodes().get(0));
            if (stmtnode.getElsetk()!=null){
                handleStmt(stmtnode.getStmtNodes().get(1));
            }
        }
        //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
        else if (stmtnode.getFortk()!=null) {
            if (stmtnode.getForStmt1()!=null) handleForStmt(stmtnode.getForStmt1());
            if (stmtnode.getCondNode()!=null) handleCond(stmtnode.getCondNode());
            if (stmtnode.getForStmt2()!=null) handleForStmt(stmtnode.getForStmt2());
            handleStmt(stmtnode.getStmtNodes().get(0));
        }
        //| 'break' ';' | 'continue' ';'
        else if(stmtnode.getBreaktkOrcontinuetk()!=null){
            if (stmtnode.getBreaktkOrcontinuetk().getType()== TokenType.BREAKTK){

            } else{

            }
        }
        //| 'return' [Exp] ';' // 1.有Exp 2.无Exp
        else if (stmtnode.getReturntk()!=null) {
            SymbolInfo.ReturnType temp=getReturnType();
            if (temp==SymbolInfo.ReturnType.VOID){
                if (stmtnode.getExpNode()!=null){
                    error.addError(error.errorType.f,stmtnode.getReturntk().getLineNumber());
                }
            }
            else{
                //这里是否需要还是个未知数
                if (stmtnode.getExpNode()==null){
                    error.addError(error.errorType.g,stmtnode.getReturntk().getLineNumber());
                }
            }
            if (stmtnode.getExpNode()!=null)    handleExp(stmtnode.getExpNode());
        }
        //| LVal '=' 'getint''('')'';'
        else if (stmtnode.getGetinttk()!=null){
            handleLVal(stmtnode.getLvalnode());
        }
        //| 'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
        else if(stmtnode.getPrintftk()!=null){
            //stmtnode.getFormatStringtk()
            for (ExpNode expNode: stmtnode.getExpNodes()){
                handleExp(expNode);
            }
        }
        //| [Exp] ';'
        else{
            if (stmtnode.getExpNode()!=null) handleExp(stmtnode.getExpNode());
        }
    }

    private void handleForStmt(ForStmtNode forStmt) {
        //ForStmt → LVal '=' Exp
        handleLVal(forStmt.getlValNode());
        handleExp(forStmt.getExpNode());
    }
    private void handleExp(ExpNode expNode) {
        // Exp → AddExp
        handleAddExp(expNode.getAddExpNode());
    }
    private void handleCond(CondNode condNode) {
        // Cond → LOrExp
        handleLOrExp(condNode.getlOrExpNode());
    }
    private void handleLVal(LValNode lvalnode) {
        //LVal → Ident {'[' Exp ']'}
        if (currentNode.getSymbol(lvalnode.getIdent().getValue())==null){
            error.addError(error.errorType.c,lvalnode.getIdent().getLineNumber());
            return;
        }
        for (ExpNode expNode: lvalnode.getExpNodes()){
            handleExp(expNode);
        }
    }
    private void handleLOrExp(LOrExpNode lOrExpNode) {
        // LOrExp → LAndExp { '||' LAndExp}
        for (LAndExpNode lAndExpNode:lOrExpNode.getlAndExpNodes()){
            handleLAndExp(lAndExpNode);
        }
    }

    private void handleLAndExp(LAndExpNode lAndExpNode) {
        //LAndExp → EqExp {'&&' EqExp}
        for (EqExpNode eqExpNode:lAndExpNode.getEqExpNodes()){
            handleEqExp(eqExpNode);
        }
    }

    private void handleEqExp(EqExpNode eqExpNode) {
        //EqExp → RelExp{ ('==' | '!=') RelExp }
        for (RelExpNode relExpNode:eqExpNode.getRelExpNodes()){
            handleRelExp(relExpNode);
        }
    }

    private void handleRelExp(RelExpNode relExpNode) {
        //RelExp → AddExp {('<' | '>' | '<=' | '>=') AddExp}
        for (AddExpNode addExpNode: relExpNode.getAddExpNodes()){
            handleAddExp(addExpNode);
        }
    }

    private void handleAddExp(AddExpNode addExpNode) {
        //AddExp → MulExp{ ('+' | '−') MulExp}
        for (MulExpNode mulExpNode: addExpNode.getMulExpNodes()){
            handleMulExp(mulExpNode);
        }
    }

    private void handleMulExp(MulExpNode mulExpNode) {
        // MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
        for (UnaryExpNode unaryExpNode: mulExpNode.getUnaryExpNodes()){
            handleUnaryExp(unaryExpNode);
        }
    }

    private void handleUnaryExp(UnaryExpNode unaryExpNode) {
        //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'
        if (unaryExpNode.getPrimaryExpNode()!=null)     handlePaimaryExp(unaryExpNode.getPrimaryExpNode());
        else{
            if (currentNode.getSymbol(unaryExpNode.getIdent().getValue())==null){
                error.addError(error.errorType.c,unaryExpNode.getIdent().getLineNumber());
                return;
            }
            //如果函数参数个数不匹配
            if (unaryExpNode.getFuncRParamsNode()==null){
                if (currentNode.getSymbol(unaryExpNode.getIdent().getValue()).getFuncParams().size()!=0){
                    error.addError(error.errorType.d,unaryExpNode.getIdent().getLineNumber());
                    return;
                }
            }
            else if (currentNode.getSymbol(unaryExpNode.getIdent().getValue()).getFuncParams().size()!=unaryExpNode.getFuncRParamsNode().getExpNodes().size()){
                error.addError(error.errorType.d,unaryExpNode.getIdent().getLineNumber());
                return;
            }
            //函数参数类型不匹配
            if (unaryExpNode.getFuncRParamsNode()!=null){
                handleFuncRParams(unaryExpNode.getFuncRParamsNode());
                SymbolInfo symbolInfo=currentNode.getSymbol(unaryExpNode.getIdent().getValue());
                for (int i=0;i<symbolInfo.getFuncParams().size();i++){
                    if (symbolInfo.getFuncParams().get(i).getType()!=getFuncRParamInExp(unaryExpNode.getFuncRParamsNode().getExpNodes().get(i))){
                        error.addError(error.errorType.e,unaryExpNode.getIdent().getLineNumber());
                    }
                }
            }
        }
    }

    private void handleFuncRParams(FuncRParamsNode funcRParamsNode) {
        //FuncRParams → Exp { ',' Exp }
        for (ExpNode expNode: funcRParamsNode.getExpNodes()){
            handleExp(expNode);
        }
    }

    private void handlePaimaryExp(PrimaryExpNode primaryExpNode) {
        //PrimaryExp → '(' Exp ')' | LVal | Number 
        if (primaryExpNode.getExpNode()!=null){
            handleExp(primaryExpNode.getExpNode());
        } else if (primaryExpNode.getlValNode()!=null) {
            handleLVal(primaryExpNode.getlValNode());
        }
        else{
            handleNumber(primaryExpNode.getNumberNode());
        }
    }

    private void handleNumber(NumberNode numberNode) {
    }


    private void handleConstExp(ConstExpNode constExpNode) {
        //ConstExp → AddExp
        handleAddExp(constExpNode.getAddExpNode());
    }









   
}
