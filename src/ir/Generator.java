package ir;

import Token.TokenType;
import ir.Basic.BasicBlock;
import ir.Basic.Const;
import ir.Basic.Function;
import ir.Basic.GlobalVar;
import ir.Instruction.OpCode;
import ir.Instruction.RetInstruction;
import ir.Type.DataType;
import ir.Type.ValueType;
import node.*;
import symbol.SymbolInfo;
import symbol.SymbolTableNode;

import java.util.HashMap;
import java.util.List;

public class Generator {
    private SymbolTableNode currentNode=SymbolTableNode.getCurrentNode();
    private SymbolInfo currentFunction=null;
    private SymbolInfo returnNum=null;
    private HashMap<String,Function> functionList= Function.getFunctions();
    private ValueTable currentValueTable=new ValueTable();
    private static Integer index=1;
    private StringBuilder irCode=new StringBuilder();
    private Value tmpValue=null;
    private BuildFactory buildFactory = BuildFactory.getBuildFactory();
    private static Generator generator=new Generator();
    private static Module currentModule=Module.getModule();
    private BasicBlock currentBasicBlock=null;
    public void Generator(){}
    public Value addBinaryInstruction(Value value1, Value value2, OpCode op) {
        if(currentBasicBlock==null){
            if ((value1 instanceof Const||value1 instanceof GlobalVar )&& (value2 instanceof Const||value2 instanceof GlobalVar)){
                int temp1,temp2;
                if (value1 instanceof GlobalVar){
                    temp1=((GlobalVar) value1).getNum();
                }
                else{
                    temp1=Integer.parseInt(value1.getName());
                }
                if (value2 instanceof GlobalVar) {
                    temp2=((GlobalVar) value2).getNum();
                }
                else{
                    temp2=Integer.parseInt(value2.getName());
                }
                if (op==OpCode.add){
                    return buildFactory.createConst(Integer.toString(temp1+temp2));
                }
                else if (op == OpCode.sub) {
                    return buildFactory.createConst(Integer.toString(temp1-temp2));
                }
                else if (op == OpCode.mul) {
                    return buildFactory.createConst(Integer.toString(temp1*temp2));
                }
                else if (op == OpCode.mod){
                    return buildFactory.createConst(Integer.toString(temp1%temp2));
                }
                else{
                    return buildFactory.createConst(Integer.toString(temp1/temp2));
                }
            }
            else {
//            Value newValue1 = zext(value1);
//            Value newValue2 = zext(value2);
                User user = new User(buildFactory.getId(),ValueType.i32);
                buildFactory.createBinaryInst(currentBasicBlock,value1,value2, user,op);
                return user;
            }
        }
        if ((value1 instanceof Const )&& (value2 instanceof Const)){
            int temp1,temp2;
                temp1=Integer.parseInt(value1.getName());
                temp2=Integer.parseInt(value2.getName());
            if (op==OpCode.add){
                return buildFactory.createConst(Integer.toString(temp1+temp2));
            }
            else if (op == OpCode.sub) {
                return buildFactory.createConst(Integer.toString(temp1-temp2));
            }
            else if (op == OpCode.mul) {
                return buildFactory.createConst(Integer.toString(temp1*temp2));
            }
            else if (op == OpCode.mod){
                return buildFactory.createConst(Integer.toString(temp1%temp2));
            }
            else{
                return buildFactory.createConst(Integer.toString(temp1/temp2));
            }
        }
        else {
//            Value newValue1 = zext(value1);
//            Value newValue2 = zext(value2);
            User user = new User(buildFactory.getId(),ValueType.i32);
            buildFactory.createBinaryInst(currentBasicBlock,value1,value2, user,op);
            return user;
        }
    }

    public void start(CompUnitNode compUnitNode){
        for (DeclNode declNode: compUnitNode.getDeclNodes()){
            handleDecl(declNode);
        }
        handleMainFunc(compUnitNode.getMainFuncDefNode());
       // System.out.println(irCode);
    }

    private void handleMainFunc(MainFuncDefNode mainFuncDefNode) {
        Function getint= new Function("getint",DataType.i32);
        Function putint= new Function("putint",DataType.VOID);
        putint.addParameter(new Parameter(DataType.i32));
        Function putch= new Function("putch",DataType.VOID);
        putch.addParameter(new Parameter(DataType.i32));
        Function putstr= new Function("putstr",DataType.VOID);
        putstr.addParameter(new Parameter(DataType.i8_));
        currentModule.addFunction(getint);
        currentModule.addFunction(putint);
        currentModule.addFunction(putch);
        currentModule.addFunction(putstr);
        functionList.add(getint);
        functionList.add(putint);
        functionList.add(putch);
        functionList.add(putstr);
        Function function=new Function("main", DataType.i32);
        function.setDefined();
        currentModule.addFunction(function);
        BasicBlock block=new BasicBlock();
        currentBasicBlock=block;
        function.addBasicBlock(block);
        handleBlock(mainFuncDefNode.getBlockNode());
    }

    private void handleBlock(BlockNode blockNode) {
        //每次进入一个新的block相当于要换一个作用域
        ValueTable valueTable=new ValueTable();
        currentValueTable.sons.add(valueTable);
        valueTable.father=currentValueTable;
        currentValueTable=valueTable;
        for (BlockItemNode blockItem : blockNode.getBlockItemNodes()){
            handleBlockItem(blockItem);
        }
        currentValueTable=currentValueTable.father;
    }

    private void handleBlockItem(BlockItemNode blockItem) {
        if (blockItem.getDeclnode()!=null) { handleDecl(blockItem.getDeclnode()); }
        else handleStmt(blockItem.getStmtnode());
    }

    private void handleStmt(StmtNode stmtnode) {
        //| 'return' [Exp] ';'
        if (stmtnode.getReturntk()!=null) {
            if (stmtnode.getExpNode()!=null) {
                Value value=handleExp(stmtnode.getExpNode());
                buildFactory.createRetInst(currentBasicBlock,value,DataType.i32);
            }
            else{
                buildFactory.createRetInst(currentBasicBlock,new Value(),DataType.VOID);
            }
        }
        //| Block
        else if (stmtnode.getBlockNode()!=null){
            handleBlock(stmtnode.getBlockNode());
        }
        //LVal
        else if(stmtnode.getLvalnode() != null){
            //LVal '=' Exp ';'
            if (stmtnode.getExpNode() != null){
                Value tempValue=handleLVal(stmtnode.getLvalnode(),true);
                if (stmtnode.getExpNode() != null){
                    Value initValue=handleExp(stmtnode.getExpNode());
                    buildFactory.createStoreInst(currentBasicBlock,initValue,tempValue);
                }
            }

            //| LVal '=' 'getint''('')'';'
            else{
                Value tempValue=handleLVal(stmtnode.getLvalnode(),true);
                buildFactory.createCallInst(currentBasicBlock,functionList.);
            }
        }
        //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        else if(stmtnode.getIftk() != null){

        }
        //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
        else if(stmtnode.getFortk() != null){

        }
        //| 'break' ';' | 'continue' ';'
        else if(stmtnode.getBreaktkOrcontinuetk()!=null){

        }
        //| 'printf''('FormatString{','Exp}')'';'
        else if(stmtnode.getPrintftk()!=null){

        }
        //| [Exp] ';'
        else{

        }
    }

    private Value handleExp(ExpNode expNode) {
        return handleAddExp(expNode.getAddExpNode());
    }

    private Value handleAddExp(AddExpNode addExpNode) {
        //AddExp → MulExp { ('+' | '−') MulExp}
        if (addExpNode.getMulExpNodes().size()==1){
            return handleMulExp(addExpNode.getMulExpNodes().get(0));
        }
        else{
            Value value1=handleMulExp(addExpNode.getMulExpNodes().get(0));
            Value value2=handleMulExp(addExpNode.getMulExpNodes().get(1));
            Value user=addBinaryInstruction(value1,value2,addExpNode.getPluseOrminus().get(0).getType()==TokenType.PLUS ? OpCode.add: OpCode.sub);
            for (int i=2;i<addExpNode.getMulExpNodes().size(); i++){
                Value tempValue=handleMulExp(addExpNode.getMulExpNodes().get(i));
                user=addBinaryInstruction(user,tempValue,addExpNode.getPluseOrminus().get(i-1).getType()==TokenType.PLUS ? OpCode.add: OpCode.sub);
            }
            return user;
        }
    }

    private Value handleMulExp(MulExpNode mulExpNode) {
        //MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp}
        if (mulExpNode.getUnaryExpNodes().size()==1){
            return handleUnaryExp(mulExpNode.getUnaryExpNodes().get(0));
        }
        else{
            Value value1=handleUnaryExp(mulExpNode.getUnaryExpNodes().get(0));
            Value value2=handleUnaryExp(mulExpNode.getUnaryExpNodes().get(1));
            OpCode op=OpCode.Token2Op(mulExpNode.getOps().get(0).getType());
            Value user=addBinaryInstruction(value1,value2,op);
            for (int i=2;i<mulExpNode.getUnaryExpNodes().size();i++){
                Value tempValue=handleUnaryExp(mulExpNode.getUnaryExpNodes().get(i));
                op=OpCode.Token2Op(mulExpNode.getOps().get(i-1).getType());
                user=addBinaryInstruction(user,tempValue,op);
            }
            return user;
        }
    }

    private Value handleUnaryExp(UnaryExpNode unaryExpNode) {
        // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        if (unaryExpNode.getPrimaryExpNode()!=null){
            return handleParimaryExp(unaryExpNode.getPrimaryExpNode());
        } else if (unaryExpNode.getIdent()!=null) {
            return null;
        }
        else{
            if (unaryExpNode.getUnaryOpNode().getMinu()!=null) {
                Value value1=buildFactory.createConst("0");
                Value value2=handleUnaryExp(unaryExpNode.getUnaryExpNode());
                Value user=addBinaryInstruction(value1,value2,OpCode.sub);
                return user;
            }
            else if(unaryExpNode.getUnaryOpNode().getPlus() != null){
                return handleUnaryExp(unaryExpNode.getUnaryExpNode());
            }
            else{
                return null;
            }
        }
    }

    private Value handleParimaryExp(PrimaryExpNode primaryExpNode) {
        //PrimaryExp → '(' Exp ')' | LVal | Number
        if (primaryExpNode.getNumberNode() != null){
            return handleNumber(primaryExpNode.getNumberNode());
        }
        else if (primaryExpNode.getlValNode() != null) {
            return handleLVal(primaryExpNode.getlValNode());
        }
        else{
            return handleExp(primaryExpNode.getExpNode());
        }
    }

    private Value handleLVal(LValNode lValNode) {
        //LVal → Ident {'[' Exp ']'}'
        if (currentBasicBlock == null){
            return currentValueTable.searchValue(lValNode.getIdent().getValue());
        }
        if (lValNode.getExpNodes().size()==0){
            Value value=currentValueTable.searchValue(lValNode.getIdent().getValue());
            User user=new User(buildFactory.getId(), value.getType());
            buildFactory.createLoadInst(currentBasicBlock,user,value);
            return user;
        }
        else{
            return null;
        }
    }
    private Value handleLVal(LValNode lvalNode,Boolean left){
        Value value=currentValueTable.searchValue(lvalNode.getIdent().getValue());
        return value;
    }
    private Value handleNumber(NumberNode numberNode) {
        return  buildFactory.createConst(numberNode.getNumber().getValue());

    }

    private void handleDecl(DeclNode declnode) {
        if (declnode.getConstDeclNode()!=null) handleConstDecl(declnode.getConstDeclNode());
        else handleVarDecl(declnode.getVarDeclNode());
    }
    private void handleConstDecl(ConstDeclNode constDeclNode) {
        //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        handleConstDef(constDeclNode.getConstDefNode(),constDeclNode.getbTypeNode());
        for (ConstDefNode constDefNode:constDeclNode.getConstDefNodes()){
            handleConstDef(constDefNode,constDeclNode.getbTypeNode());
        }
    }

    private void handleConstDef(ConstDefNode constDefNode,BTypeNode btype){
        //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        //这次先不管数组这玩意
        if (currentValueTable.father==null){
            if (constDefNode.getConstExpNodes().size()==0){
                GlobalVar globalVar = null;
                if (btype.getInttk()!=null) {
                    globalVar=buildFactory.createGlobalVar(constDefNode.getIdent().getValue(), ValueType.i32, true);
                }
                int num = ((Const)handleConstExp(constDefNode.getConstInitValNode().getConstExpNode())).getValue();
                globalVar.setNum(num);
                currentValueTable.addValue(constDefNode.getIdent().getValue(),globalVar);
            }
        }
        else{
            if(constDefNode.getConstExpNodes().size()==0){
                User user=null;
                Value param=new Value();
                if(btype.getInttk()!=null) {
                    user=new User(buildFactory.getId(),ValueType.i32);
                    param.setType(ValueType.i32);
                }
                buildFactory.createAllocateInst(currentBasicBlock,user,param);
                currentValueTable.addValue(constDefNode.getIdent().getValue(),user);
            }
        }
    }
    private void handleVarDecl(VarDeclNode varDeclNode) {
        //VarDecl → BType VarDef { ',' VarDef } ';'
        handleVarDef(varDeclNode.getVarDefNode(),varDeclNode.getbTypeNode());
        for (VarDefNode varDefNode:varDeclNode.getVarDefNodes()){
            handleVarDef(varDefNode,varDeclNode.getbTypeNode());
        }
    }

    private void handleVarDef(VarDefNode varDefNode, BTypeNode bTypeNode) {
        //VarDef → Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
        //是否为全局变量
        if (currentValueTable.father==null){
            //不是数组
            if (varDefNode.getConstExpNodes().size()==0){
                GlobalVar globalVar = null;
                if (bTypeNode.getInttk()!=null) {
                    globalVar=buildFactory.createGlobalVar(varDefNode.getIdent().getValue(), ValueType.i32, false);
                }
                int num=0;
                if (varDefNode.getInitValNode()!=null){
                    num = ((Const)handleExp(varDefNode.getInitValNode().getExpNode())).getValue();
                }
                globalVar.setNum(num);
                currentValueTable.addValue(varDefNode.getIdent().getValue(),globalVar);
            }
        }
        else{
            if(varDefNode.getConstExpNodes().size()==0){
                User user=null;
                Value param=new Value();
                if(bTypeNode.getInttk()!=null) {
                    user=new User(buildFactory.getId(),ValueType.i32);
                    param.setType(ValueType.i32);
                }
                buildFactory.createAllocateInst(currentBasicBlock,user,param);
                //还得给这个变量赋值
                if (varDefNode.getInitValNode() != null){
                    handleInitial(user,varDefNode.getInitValNode());
                }
                currentValueTable.addValue(varDefNode.getIdent().getValue(),user);
            }
        }
    }

    private void handleInitial(Value store,InitValNode initValNode) {
        //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'/
        Value initVal= handleExp(initValNode.getExpNode());
        Value tempValue=store;
        buildFactory.createStoreInst(currentBasicBlock,initVal,tempValue);
    }


    private void handleConstInitVal(ConstInitValNode constInitValNode) {
    //ConstInitVal → ConstExp| '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        handleConstExp(constInitValNode.getConstExpNode());
    }

    private Value handleConstExp(ConstExpNode constExpNode) {
        //ConstExp     → AddExp
        return handleAddExp(constExpNode.getAddExpNode());
    }

    private void handleBtype(BTypeNode bTypeNode) {

    }

}
