package ir;

import Token.TokenType;
import ir.Basic.BasicBlock;
import ir.Basic.Const;
import ir.Basic.Function;
import ir.Basic.GlobalVar;
import ir.Instruction.CallInstruction;
import ir.Instruction.OpCode;
import ir.Type.ValueType;
import node.*;
import symbol.SymbolInfo;
import symbol.SymbolTableNode;

import java.util.HashMap;
import java.util.List;

public class Generator {
    private SymbolTableNode currentNode=SymbolTableNode.getCurrentNode();
    private Function currentFunction=null;
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
        //CompUnit    → {Decl} {FuncDef} MainFuncDef
        Function getint= new Function("getint",ValueType.i32);
        Function putint= new Function("putint",ValueType.VOID);
        putint.addParameter(new Parameter(ValueType.i32));
        Function putch= new Function("putch",ValueType.VOID);
        putch.addParameter(new Parameter(ValueType.i32));
        Function putstr= new Function("putstr",ValueType.VOID);
        putstr.addParameter(new Parameter(ValueType.i8_));
        currentModule.addFunction(getint);
        currentModule.addFunction(putint);
        currentModule.addFunction(putch);
        currentModule.addFunction(putstr);
        functionList.put("getint",getint);
        functionList.put("putint",putint);
        functionList.put("putch",putch);
        functionList.put("putstr",putstr);
        for (DeclNode declNode: compUnitNode.getDeclNodes()){
            handleDecl(declNode);
        }
        for (FuncDefNode funcDefNode: compUnitNode.getFuncDefNodes()){
            handleFuncDef(funcDefNode);
        }
        handleMainFunc(compUnitNode.getMainFuncDefNode());
       // System.out.println(irCode);
    }

    private void handleFuncDef(FuncDefNode funcDefNode) {
        //FuncDef     → FuncType Ident '(' [FuncFParams] ')' Block
        buildFactory.resetId0();
        ValueType valueType=handleFuncType(funcDefNode.getFuncTypeNode());
        String name=funcDefNode.getIdent().getValue();
        Function function=new Function(name,valueType);
        currentFunction=function;
        BasicBlock block=new BasicBlock();
        function.addBasicBlock(block);
        currentBasicBlock=block;
        currentValueTable=currentValueTable.enterNextTbale();
        if (funcDefNode.getFuncFParamsNode()!=null) {
            handleFuncFParams(funcDefNode.getFuncFParamsNode(),function);
        }
        else{
            buildFactory.resetId1();
        }
        currentModule.addFunction(function);
        functionList.put(name,function);
        function.setDefined();

        handleBlock(funcDefNode.getBlockNode());
    }

    private void handleFuncFParams(FuncFParamsNode funcFParamsNode, Function function) {
        //FuncFParams → FuncFParam { ',' FuncFParam }
        for (int i=0;i<funcFParamsNode.getFuncFParamsNodes().size();i++) {
            Parameter parameter=handleFuncFParam(funcFParamsNode.getFuncFParamsNodes().get(i));
            function.addParameter(parameter);
        }
        //把参数表中的参数都加载到内存空间中
        //给这个基本块一个编号，由于不知道有什么用，我先简单把编号加1，后期记得改
        buildFactory.getId();
        List<Parameter> parameters=function.getParameters();
        for (int i=0;i<parameters.size();i++) {
            User user=new User(buildFactory.getId(), parameters.get(i).getType());
            buildFactory.createAllocateInst(currentBasicBlock,user,parameters.get(i));
            buildFactory.createStoreInst(currentBasicBlock,parameters.get(i),user);
            currentValueTable.addValue(funcFParamsNode.getFuncFParamsNodes().get(i).getIdent().getValue(),user);
        }
    }

    private Parameter handleFuncFParam(FuncFParamNode funcFParamNode) {
        //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        //传入的是普通变量
        if (funcFParamNode.getLbracks().size()==0){
            ValueType valueType =handleBtype(funcFParamNode.getBtypenode());
            String name=funcFParamNode.getIdent().getValue();
            Parameter parameter=new Parameter(buildFactory.getId(),valueType);
            currentValueTable.addValue(name,parameter);
            return parameter;
        }
        //一维数组
        else if (funcFParamNode.getLbracks().size() == 1){
            return null;
        }//二维数组
        else{
            return null;
        }
    }

    private ValueType handleFuncType(FuncTypeNode funcTypeNode) {
        //FuncType    → 'void' | 'int'
        if (funcTypeNode.getVoidtk()!=null){
            return ValueType.VOID;
        }
        else {
            return ValueType.i32;
        }
    }

    private void handleMainFunc(MainFuncDefNode mainFuncDefNode) {
        Function function=new Function("main", ValueType.i32);
        currentFunction=function;
        function.setDefined();
        buildFactory.resetId1();
        currentModule.addFunction(function);
        BasicBlock block=new BasicBlock();
        currentBasicBlock=block;
        function.addBasicBlock(block);
        currentValueTable=currentValueTable.enterNextTbale();
        handleBlock(mainFuncDefNode.getBlockNode());
    }

    private void handleBlock(BlockNode blockNode) {

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
                buildFactory.createRetInst(currentBasicBlock,value,ValueType.i32);
            }
            else{
                buildFactory.createRetInst(currentBasicBlock,new Value(),ValueType.VOID);
            }
        }
        //| Block
        else if (stmtnode.getBlockNode()!=null){
            currentValueTable=currentValueTable.enterNextTbale();
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
                Function getint=functionList.get("getint");
                User user=new User(buildFactory.getId(),getint.getType());
                buildFactory.createCallInst(currentBasicBlock,functionList.get("getint"),user);
                buildFactory.createStoreInst(currentBasicBlock,user,tempValue);
            }
        }
        //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        else if(stmtnode.getIftk() != null){
            BasicBlock ifblock=new BasicBlock();
            BasicBlock outblock=new BasicBlock();
            BasicBlock elseblock=new BasicBlock();
            currentFunction.addBasicBlock(ifblock);
            currentFunction.addBasicBlock(outblock);
            if (stmtnode.getElsetk() != null) {
                currentFunction.addBasicBlock(elseblock);
            }
            handleCond(stmtnode.getCondNode(),ifblock,elseblock,outblock);
        }
        //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
        else if(stmtnode.getFortk() != null){

        }
        //| 'break' ';' | 'continue' ';'
        else if(stmtnode.getBreaktkOrcontinuetk()!=null){

        }
        //| 'printf''('FormatString{','Exp}')'';'
        else if(stmtnode.getPrintftk()!=null){
            int expIndex=0;
            String str=stmtnode.getFormatStringtk().getValue();
            for (int i=0;i<str.length();i++){
                if (str.charAt(i)=='"') continue;
                if(str.charAt(i)=='%'){
                 //出现%说明有一个对应的exp,对exp进行一个处理
                    i++;
                    //出现的为int，可以再增加新的类型
                    if(str.charAt(i)=='d'){
                        Value tempValue=handleExp(stmtnode.getExpNodes().get(expIndex));
                        CallInstruction callInstruction=buildFactory.createCallInst(currentBasicBlock,functionList.get("putint"));
                        callInstruction.addParam(tempValue);
                        expIndex++;
                    }
                }
                else{
                    CallInstruction callInstruction=buildFactory.createCallInst(currentBasicBlock,functionList.get("putch"));
                    callInstruction.addParam(new Value(Integer.toString(str.charAt(i)-0),ValueType.i32));
                }

            }
        }
        //| [Exp] ';'
        else{
            if (stmtnode.getExpNode()!=null){
                handleExp(stmtnode.getExpNode());
            }
        }
    }

    private void handleCond(CondNode condNode, BasicBlock ifblock, BasicBlock elseblock, BasicBlock outblock) {
        handleLOrExp(condNode, ifblock, elseblock, outblock);
    }

    private void handleLOrExp(CondNode condNode, BasicBlock ifblock, BasicBlock elseblock, BasicBlock outblock) {
        //LOrExp → LAndExp {'||' LAndExp}

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
            Function function=functionList.get(unaryExpNode.getIdent().getValue());
            CallInstruction callInstruction=null;
            User user=null;
            if (function.getType()!=ValueType.VOID){
                user=new User(buildFactory.getId(), function.getType());
               callInstruction=buildFactory.createCallInst(currentBasicBlock,function,user);

            }
            else{
                callInstruction = buildFactory.createCallInst(currentBasicBlock,function);
            }
            if (unaryExpNode.getFuncRParamsNode() != null){
                for (ExpNode expNode : unaryExpNode.getFuncRParamsNode().getExpNodes()){
                    Value value =handleExp(expNode);
                    callInstruction.addParam(value);
                }
            }
            return user;
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

    private ValueType handleBtype(BTypeNode bTypeNode) {
        if (bTypeNode.getInttk()!=null){
            return ValueType.i32;
        }
        return null;
    }

}
