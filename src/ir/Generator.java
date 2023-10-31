package ir;

import Token.TokenType;
import ir.Basic.*;
import ir.Instruction.CallInstruction;
import ir.Instruction.OpCode;
import ir.Type.ValueType;
import node.*;
import symbol.SymbolInfo;
import symbol.SymbolTableNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Generator {
    private SymbolTableNode currentNode=SymbolTableNode.getCurrentNode();
    private Function currentFunction=null;
    private SymbolInfo returnNum=null;
    private HashMap<String,Function> functionList= Function.getFunctions();
    private ValueTable currentValueTable=new ValueTable();
    private Stack<BasicBlock> loop=new Stack<>();
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
            currentBasicBlock.setName(buildFactory.getId());
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
        currentBasicBlock.setName(buildFactory.getId());
        List<Parameter> parameters=function.getParameters();
        for (int i=0;i<parameters.size();i++) {
            User user=new User(buildFactory.getId(), parameters.get(i).getType());
            if (parameters.get(i).twoarrayNum!=null) user.setTwoarrayNum(parameters.get(i).twoarrayNum);
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
            String name=funcFParamNode.getIdent().getValue();
            Parameter parameter=new Parameter(buildFactory.getId(),ValueType.i32_);
            currentValueTable.addValue(name,parameter);
            return parameter;
        }//二维数组
        else{
            String name=funcFParamNode.getIdent().getValue();
            Parameter parameter=new Parameter(buildFactory.getId(),ValueType.i32_);
            parameter.setTwoarrayNum(((Const)handleConstExp(funcFParamNode.getConstExpNodes().get(0))).getName());
            currentValueTable.addValue(name,parameter);
            return parameter;
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
        buildFactory.resetId0();
        currentModule.addFunction(function);
        BasicBlock block=new BasicBlock();
        block.setName(buildFactory.getId());
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
            BasicBlock elseblock=null;
            currentFunction.addBasicBlock(ifblock);

            if (stmtnode.getElsetk() != null) {
                elseblock=new BasicBlock();
            }
            handleCond(stmtnode.getCondNode(),ifblock,elseblock,outblock);
            //处理ifblock
            ifblock.setName(buildFactory.getId());
            currentBasicBlock=ifblock;
            handleStmt(stmtnode.getStmtNodes().get(0));
            buildFactory.createBranchInst(currentBasicBlock,outblock);
            //如果有else
            if (stmtnode.getElsetk()!=null){
                elseblock.setName(buildFactory.getId());
                currentBasicBlock=elseblock;
                handleStmt(stmtnode.getStmtNodes().get(1));
                currentFunction.addBasicBlock(elseblock);
                buildFactory.createBranchInst(currentBasicBlock,outblock);
            }
            outblock.setName(buildFactory.getId());
            currentBasicBlock=outblock;
            currentFunction.addBasicBlock(outblock);
        }
        //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
        else if(stmtnode.getFortk() != null){
            BasicBlock loopblock=new BasicBlock();
            BasicBlock outblock=new BasicBlock();
            BasicBlock ifblock=null;
            BasicBlock forstmt=new BasicBlock();
            currentFunction.addBasicBlock(loopblock);
            if (stmtnode.getForStmt1()!=null){
                handleForStmt(stmtnode.getForStmt1());
            }
            //判断语句单独拿出来作为一个基本块
            ifblock=new BasicBlock();
            ifblock.setName(buildFactory.getId());
            currentFunction.addBasicBlock(ifblock);
            currentBasicBlock=ifblock;
            if (stmtnode.getCondNode()!=null)  handleCond(stmtnode.getCondNode(),loopblock,null,outblock);
            //设置循环基本块
            loopblock.setName(buildFactory.getId());
            currentBasicBlock=loopblock;
            loopblock.setOutBlock(outblock);
            loop.push(loopblock);
            if (stmtnode.getForStmt2()!=null) loopblock.setNextBlock(forstmt);
            else loopblock.setNextBlock(ifblock);
            handleStmt(stmtnode.getStmtNodes().get(0));
            loop.pop();
            //每次执行完循环块后执行一遍forstmt块，如果没有则不执行
            if (stmtnode.getForStmt2()!=null){
                currentFunction.addBasicBlock(forstmt);
                buildFactory.createBranchInst(currentBasicBlock,forstmt);
                forstmt.setName(buildFactory.getId());
                currentBasicBlock=forstmt;
                handleForStmt(stmtnode.getForStmt2());
                buildFactory.createBranchInst(currentBasicBlock,ifblock);
            }
            else{
                buildFactory.createBranchInst(currentBasicBlock,ifblock);
            }
            //buildFactory.createBranchInst(currentBasicBlock,);


            outblock.setName(buildFactory.getId());
            currentBasicBlock=outblock;
            currentFunction.addBasicBlock(outblock);
        }
        //| 'break' ';' | 'continue' ';'
        else if(stmtnode.getBreaktkOrcontinuetk()!=null){
            if (stmtnode.getBreaktkOrcontinuetk().getType()==TokenType.BREAKTK){
                buildFactory.createBranchInst(currentBasicBlock,loop.peek().getOutblock());
            }
            else buildFactory.createBranchInst(currentBasicBlock,loop.peek().getNextblock());
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

    private void handleForStmt(ForStmtNode forStmt1) {
        //ForStmt → LVal '=' Exp
        if (forStmt1.getlValNode()!=null){
            Value tempValue=handleLVal(forStmt1.getlValNode(),true);
            if (forStmt1.getExpNode()!=null){
                Value initValue=handleExp(forStmt1.getExpNode());
                buildFactory.createStoreInst(currentBasicBlock,initValue,tempValue);
            }
        }
    }

    private void handleCond(CondNode condNode, BasicBlock ifblock, BasicBlock elseblock, BasicBlock outblock) {
        handleLOrExp(condNode.getlOrExpNode(), ifblock, elseblock, outblock);
    }

    private void handleLOrExp(LOrExpNode lOrExpNode, BasicBlock ifblock, BasicBlock elseblock, BasicBlock outblock) {
        //LOrExp → LAndExp {'||' LAndExp}
        for (int i=0;i<lOrExpNode.getlAndExpNodes().size();i++) {
            BasicBlock judge=null;
            if (i!=lOrExpNode.getlAndExpNodes().size()-1) {
                judge=new BasicBlock();
            }
            Value value=handleLandExp(lOrExpNode.getlAndExpNodes().get(i),ifblock,elseblock,outblock,judge);
            BasicBlock falseBlock=null;
            if (value.getType()==ValueType.i32){
                User user=new User(buildFactory.getId(),ValueType.i1);
                buildFactory.createIcmpInst(currentBasicBlock,user,value,new Const("0"),OpCode.ne);
                value=user;
            }
            //如果这是最后一个或判断
            if (i==lOrExpNode.getlAndExpNodes().size()-1){
                falseBlock=(elseblock==null?outblock:elseblock);
            }
            else{
                judge.setName(buildFactory.getId());
                falseBlock=judge;
                currentFunction.addBasicBlock(judge);
            }
            if (!(lOrExpNode.getlAndExpNodes().get(i).getEqExpNodes().size()>1)) buildFactory.createBranchInst(currentBasicBlock,value,ifblock,falseBlock);
            if (i!=lOrExpNode.getlAndExpNodes().size()-1) currentBasicBlock=falseBlock;
        }
    }

    private Value handleLandExp(LAndExpNode lAndExpNode, BasicBlock ifblock, BasicBlock elseblock, BasicBlock outblock, BasicBlock nextblock) {
    //LAndExp → EqExp { '&&' EqExp}
        Value value=null;
        if(lAndExpNode.getEqExpNodes().size()==1) return handleEqExp(lAndExpNode.getEqExpNodes().get(0));
        for (int i=0;i<lAndExpNode.getEqExpNodes().size();i++) {
            value=handleEqExp(lAndExpNode.getEqExpNodes().get(i));
            if (value.getType()==ValueType.i32){
                User user=new User(buildFactory.getId(),ValueType.i1);
                buildFactory.createIcmpInst(currentBasicBlock,user,value,new Const("0"),OpCode.ne);
                value=user;
            }
            //如果这是与判断的最后一个判断
            BasicBlock trueblock=null;
            BasicBlock falseblock=null;
            BasicBlock judge=new BasicBlock();
            if (i==lAndExpNode.getEqExpNodes().size()-1){
                trueblock=ifblock;
                //如果这是cond的最后一个判断
                if (nextblock == null){
                    falseblock=elseblock==null?outblock:elseblock;
                }
                else{
                    falseblock=nextblock;
                }
            }
            else{
                trueblock=judge;
                judge.setName(buildFactory.getId());
                currentFunction.addBasicBlock(judge);
                //如果接下来没有或判断，那么这里出错就直接去else或者out
                if (nextblock == null){
                    falseblock=elseblock==null?outblock : elseblock;
                }
                else{
                    falseblock=nextblock;
                }
            }
            buildFactory.createBranchInst(currentBasicBlock,value,trueblock,falseblock);
            if (i!=lAndExpNode.getEqExpNodes().size()-1)currentBasicBlock=trueblock;
        }
        return value;
    }

    private Value handleEqExp(EqExpNode eqExpNode) {
        //EqExp → RelExp {('==' | '!=') RelExp}
        if (eqExpNode.getRelExpNodes().size()==1) return handleRelExp(eqExpNode.getRelExpNodes().get(0));
        else{
            Value value1=handleRelExp(eqExpNode.getRelExpNodes().get(0));
            Value value2=handleRelExp(eqExpNode.getRelExpNodes().get(1));
            OpCode opcode=OpCode.Token2Op(eqExpNode.getEqlOrNeqs().get(0).getType());
            User user=new User(buildFactory.getId(), ValueType.i1);
            buildFactory.createIcmpInst(currentBasicBlock,user,value1,value2,opcode);
            for (int i=2;i<eqExpNode.getRelExpNodes().size();i++){
                value1=zext(user);
                value2=handleRelExp(eqExpNode.getRelExpNodes().get(i));
                opcode=OpCode.Token2Op(eqExpNode.getEqlOrNeqs().get(i-1).getType());
                user=new User(buildFactory.getId(), ValueType.i1);
                buildFactory.createIcmpInst(currentBasicBlock,user,value1,value2,opcode);
            }
            return user;
        }
    }

    private Value handleRelExp(RelExpNode relExpNode) {
        //RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp}
        if (relExpNode.getAddExpNodes().size()==1) return handleAddExp(relExpNode.getAddExpNodes().get(0));
        else{
            Value value1=handleAddExp(relExpNode.getAddExpNodes().get(0));
            Value value2=handleAddExp(relExpNode.getAddExpNodes().get(1));
            OpCode opcode=OpCode.Token2Op(relExpNode.getOps().get(0).getType());
            User user=new User(buildFactory.getId(), ValueType.i1);
            buildFactory.createIcmpInst(currentBasicBlock,user,value1,value2,opcode);
            for (int i=2;i<relExpNode.getAddExpNodes().size();i++){
                value1=zext(user);
                value2=handleAddExp(relExpNode.getAddExpNodes().get(i));
                opcode=OpCode.Token2Op(relExpNode.getOps().get(i-1).getType());
                user=new User(buildFactory.getId(), ValueType.i1);
                buildFactory.createIcmpInst(currentBasicBlock,user,value1,value2,opcode);
            }
            return user;
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
        Value ident=currentValueTable.searchValue(lValNode.getIdent().getValue());
        if (currentBasicBlock == null){
            return ident;
        }
        if (ident.getType()==ValueType.i32){
            User user=new User(buildFactory.getId(), ident.getType());
            buildFactory.createLoadInst(currentBasicBlock,user,ident);
            return user;
        }
        //一维数组
        else if (ident.getType()==ValueType.onearray){
            if (lValNode.getExpNodes().size()==1){
                Value value1=handleExp(lValNode.getExpNodes().get(0));
                Value user=buildFactory.createGetElementPtr(currentBasicBlock,ident,new Const("0"),value1);
                User tempuser=new User(buildFactory.getId(), ValueType.i32);
                user.setType(ValueType.i32);
                buildFactory.createLoadInst(currentBasicBlock,tempuser,user);
                return tempuser;
            }
            //虽然ident是一维数组，但是我取的就是一维数组的数组地址
            else{
                Value user=buildFactory.createGetElementPtr(currentBasicBlock,ident,new Const("0"),new Const("0"));
                user.setType(ValueType.i32_);
                User tempuser=new User(buildFactory.getId(), ValueType.onearray);
                //buildFactory.createLoadInst(currentBasicBlock,tempuser,user);
                return user;
            }

        } else if (ident.getType()==ValueType.twoarray) {
            if (lValNode.getExpNodes().size()==2){
                Value value1=handleExp(lValNode.getExpNodes().get(0));
                Value value2=handleExp(lValNode.getExpNodes().get(1));
                Value temp=addBinaryInstruction(value1,new Const(ident.twoarrayNum),OpCode.mul);
                Value index=addBinaryInstruction(temp,value2,OpCode.add);
                Value user=buildFactory.createGetElementPtr(currentBasicBlock,ident,new Const("0"),index);
                User tempuser=new User(buildFactory.getId(), ValueType.i32);
                buildFactory.createLoadInst(currentBasicBlock,tempuser,user);
                return tempuser;
            }//取二维数组的一维地址
            else if (lValNode.getExpNodes().size()==1){
                Value value1=handleExp(lValNode.getExpNodes().get(0));
                Value temp=addBinaryInstruction(value1,new Const(ident.twoarrayNum),OpCode.mul);
                Value user=buildFactory.createGetElementPtr(currentBasicBlock,ident,new Const("0"),temp);
                user.setType(ValueType.i32_);
                return user;
            }
            else{
                Value user=buildFactory.createGetElementPtr(currentBasicBlock,ident,new Const("0"),new Const("0"));
                user.setType(ValueType.i32_);
                return user;
            }
        }
        //在函数中可能出现的指针类型
        else if (ident.getType()==ValueType.i32_) {
            if (lValNode.getExpNodes().size()==1){
                User tempuser=new User(buildFactory.getId(), ValueType.i32_);
                buildFactory.createLoadInst(currentBasicBlock,tempuser,ident);
                Value value1=handleExp(lValNode.getExpNodes().get(0));
                Value user=buildFactory.createGetElementPtr(currentBasicBlock,tempuser,value1);
                User user2=new User(buildFactory.getId(),ValueType.i32);
                buildFactory.createLoadInst(currentBasicBlock,user2,user);
                return user2;
            }
            else {
                User tempuser=new User(buildFactory.getId(), ValueType.i32_);
                buildFactory.createLoadInst(currentBasicBlock,tempuser,ident);
                Value value1=handleExp(lValNode.getExpNodes().get(0));
                Value value2=handleExp(lValNode.getExpNodes().get(1));
                Const tempconst=new Const(ident.twoarrayNum);
                Value temp=addBinaryInstruction(value1,tempconst,OpCode.mul);
                Value index=addBinaryInstruction(temp,value2,OpCode.add);
                Value user=buildFactory.createGetElementPtr(currentBasicBlock,tempuser,index);
                User user2=new User(buildFactory.getId(),ValueType.i32);
                buildFactory.createLoadInst(currentBasicBlock,user2,user);
                return user2;
            }
        } else{
            return null;
        }
    }
    private Value handleLVal(LValNode lvalNode,Boolean left){
        Value value=currentValueTable.searchValue(lvalNode.getIdent().getValue());
        if (value.getType()==ValueType.i32){
            return value;
        }
        //LVal → Ident {'[' Exp ']'}'
        else if (value.getType()==ValueType.onearray){
            Value array=currentValueTable.searchValue(lvalNode.getIdent().getValue());
            Value value1=handleExp(lvalNode.getExpNodes().get(0));
            Value user=buildFactory.createGetElementPtr(currentBasicBlock,array,new Const("0"),value1);
            return user;
        }
        else if (value.getType()==ValueType.twoarray){
            Value array=currentValueTable.searchValue(lvalNode.getIdent().getValue());
            Value value1=handleExp(lvalNode.getExpNodes().get(0));
            Value value2=handleExp(lvalNode.getExpNodes().get(1));
            Value temp=addBinaryInstruction(value1,new Const(array.twoarrayNum),OpCode.mul);
            Value index=addBinaryInstruction(temp,value2,OpCode.add);
            Value user=buildFactory.createGetElementPtr(currentBasicBlock,array,new Const("0"),index);
            return user;
        }
        else{
            return null;
        }
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
            //一维数组
            else if (constDefNode.getConstExpNodes().size()==1){
                GlobalVar globalVar = null;
                if (btype.getInttk()!=null) {
                    globalVar=buildFactory.createGlobalVar(constDefNode.getIdent().getValue(), ValueType.onearray, true);
                }
                globalVar.setOnearrayNum(((Const)handleConstExp(constDefNode.getConstExpNodes().get(0))).getName());
                List<String> initValue=handleConstInitVal(constDefNode.getConstInitValNode());
                globalVar.setArrayNum(initValue);
                currentValueTable.addValue(constDefNode.getIdent().getValue(),globalVar);
            }
            //二维数组
            else{
                GlobalVar globalVar=null;
                if (btype.getInttk()!=null){
                    globalVar=buildFactory.createGlobalVar(constDefNode.getIdent().getValue(), ValueType.twoarray, true);
                }
                globalVar.setOnearrayNum(((Const)handleConstExp(constDefNode.getConstExpNodes().get(0))).getName());
                globalVar.setTwoarrayNum(((Const)handleConstExp(constDefNode.getConstExpNodes().get(1))).getName());
                List<String> initValue=handleConstInitVal(constDefNode.getConstInitValNode());
                globalVar.setArrayNum(initValue);
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
                handleConstInitVal(user,constDefNode.getConstInitValNode());
                currentValueTable.addValue(constDefNode.getIdent().getValue(),user);
            }
            //一维数组
            else if (constDefNode.getConstExpNodes().size()==1){
                User user=null;
                Value param=new Value();
                if (btype.getInttk()!=null){
                    user= new User(buildFactory.getId(), ValueType.onearray);
                    param.setType(ValueType.onearray);
                    user.setOnearrayNum(((Const)handleConstExp(constDefNode.getConstExpNodes().get(0))).getName());
                    param.setOnearrayNum(((Const)handleConstExp(constDefNode.getConstExpNodes().get(0))).getName());
                }
                buildFactory.createAllocateInst(currentBasicBlock,user,param);
                handleConstInitVal(user,constDefNode.getConstInitValNode());
                currentValueTable.addValue(constDefNode.getIdent().getValue(),user);
            }
            //二维数组
            else {
                User user=null;
                Value param=new Value();
                if (btype.getInttk()!=null){
                    user= new User(buildFactory.getId(), ValueType.twoarray);
                    param.setType(ValueType.twoarray);
                    user.setOnearrayNum(((Const)handleConstExp(constDefNode.getConstExpNodes().get(0))).getName());
                    param.setOnearrayNum(((Const)handleConstExp(constDefNode.getConstExpNodes().get(0))).getName());
                    user.setTwoarrayNum(((Const)handleConstExp(constDefNode.getConstExpNodes().get(1))).getName());
                    param.setTwoarrayNum(((Const)handleConstExp(constDefNode.getConstExpNodes().get(1))).getName());
                }
                buildFactory.createAllocateInst(currentBasicBlock,user,param);
                handleConstInitVal(user,constDefNode.getConstInitValNode());
                currentValueTable.addValue(constDefNode.getIdent().getValue(),user);
            }
        }
    }

    private void handleConstInitVal(Value user, ConstInitValNode constInitValNode) {
        //ConstInitVal → ConstExp| '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        if (constInitValNode.getConstExpNode()!=null){
            Value value=handleConstExp(constInitValNode.getConstExpNode());
            buildFactory.createStoreInst(currentBasicBlock,value,user);
        }
        //一维数组
        else if (user.getType()==ValueType.onearray){
            for (int i=0;i<constInitValNode.getConstInitValNodes().size();i++){
                Value tempValue=handleConstExp(constInitValNode.getConstInitValNodes().get(i).getConstExpNode());
                //先把数组对应的地址取出来
                Value pointer=buildFactory.createGetElementPtr(currentBasicBlock,user,new Const("0"),new Const(Integer.toString(i)));
                buildFactory.createStoreInst(currentBasicBlock,tempValue,pointer);
            }
        }
        //二维数组
        else{
            for (int i=0;i<constInitValNode.getConstInitValNodes().size();i++){
                for (int j=0;j<constInitValNode.getConstInitValNodes().get(i).getConstInitValNodes().size();j++){
                    Value tempValue=handleConstExp(constInitValNode.getConstInitValNodes().get(i).getConstInitValNodes().get(j).getConstExpNode());
                    //先把数组对应的地址取出来
                    Integer index=i*Integer.parseInt(user.twoarrayNum)+j;
                    Value pointer=buildFactory.createGetElementPtr(currentBasicBlock,user,new Const("0"),new Const(Integer.toString(index)));
                    buildFactory.createStoreInst(currentBasicBlock,tempValue,pointer);
                }
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
            //一维数组
            else if (varDefNode.getConstExpNodes().size()==1){
                GlobalVar globalVar = null;
                if (bTypeNode.getInttk()!=null) {
                    globalVar=buildFactory.createGlobalVar(varDefNode.getIdent().getValue(), ValueType.onearray, false);
                }
                List<String> initValue=null;
                if (varDefNode.getInitValNode()!=null){
                    initValue=handleInitial(varDefNode.getInitValNode());
                }
                globalVar.setOnearrayNum(((Const)handleConstExp(varDefNode.getConstExpNodes().get(0))).getName());
                globalVar.setArrayNum(initValue);
                currentValueTable.addValue(varDefNode.getIdent().getValue(),globalVar);
            }
            //二维数组
            else{
                GlobalVar globalVar=null;
                if (bTypeNode.getInttk()!=null){
                    globalVar=buildFactory.createGlobalVar(varDefNode.getIdent().getValue(), ValueType.twoarray, false);
                }
                List<String> initValue=null;
                if (varDefNode.getInitValNode()!=null){
                    initValue=handleInitial(varDefNode.getInitValNode());
                }
                globalVar.setOnearrayNum(((Const)handleConstExp(varDefNode.getConstExpNodes().get(0))).getName());
                globalVar.setTwoarrayNum(((Const)handleConstExp(varDefNode.getConstExpNodes().get(1))).getName());
                globalVar.setArrayNum(initValue);
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
            //一维数组
            //VarDef → Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
            else if (varDefNode.getConstExpNodes().size()==1){
                User user=null;
                Value param=new Value();
                if (bTypeNode.getInttk()!=null){
                    user= new User(buildFactory.getId(), ValueType.onearray);
                    param.setType(ValueType.onearray);
                    Value value=handleConstExp(varDefNode.getConstExpNodes().get(0));
                    user.setOnearrayNum(((Const)value).getName());
                    param.setOnearrayNum(((Const)value).getName());
                }
                buildFactory.createAllocateInst(currentBasicBlock,user,param);
                if (varDefNode.getInitValNode() != null){
                    handleInitial(user,varDefNode.getInitValNode());
                }
                currentValueTable.addValue(varDefNode.getIdent().getValue(),user);
            }
            //二维数组
            else{
                User user=null;
                Value param=new Value();
                if (bTypeNode.getInttk()!=null){
                    user= new User(buildFactory.getId(), ValueType.twoarray);
                    param.setType(ValueType.twoarray);
                    Value value=handleConstExp(varDefNode.getConstExpNodes().get(0));
                    user.setOnearrayNum(((Const)value).getName());
                    param.setOnearrayNum(((Const)value).getName());
                    Value value1=handleConstExp(varDefNode.getConstExpNodes().get(1));
                    user.setTwoarrayNum(((Const)value1).getName());
                    param.setTwoarrayNum(((Const)value1).getName());
                }
                buildFactory.createAllocateInst(currentBasicBlock,user,param);
                if (varDefNode.getInitValNode() != null){
                    handleInitial(user,varDefNode.getInitValNode());
                }
                currentValueTable.addValue(varDefNode.getIdent().getValue(),user);
            }
        }
    }
    private List<String> handleInitial(InitValNode initValNode) {
        //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        if (initValNode.getExpNode()!=null){
            List<String> list=new ArrayList<>();
            list.add(((Const)handleExp(initValNode.getExpNode())).getName());
            return list;
        }
        else{
            List<String> list=new ArrayList<>();
            for (InitValNode node:initValNode.getInitValNodes()){
                list.addAll(handleInitial(node));
            }
            return list;
        }
    }
    private void handleInitial(Value store,InitValNode initValNode) {
        //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'/
        if (initValNode.getExpNode()!=null){
            Value initVal= handleExp(initValNode.getExpNode());
            Value tempValue=store;
            buildFactory.createStoreInst(currentBasicBlock,initVal,tempValue);
        }
        else if (store.getType()==ValueType.onearray){
            for (int i=0;i<initValNode.getInitValNodes().size();i++){
                Value tempValue=handleExp(initValNode.getInitValNodes().get(i).getExpNode());
                //先把数组对应的地址取出来
                Value pointer=buildFactory.createGetElementPtr(currentBasicBlock,store,new Const("0"),new Const(Integer.toString(i)));
                buildFactory.createStoreInst(currentBasicBlock,tempValue,pointer);
            }
        }
        //二维数组
        else{
            for (int i=0;i<initValNode.getInitValNodes().size();i++){
                for (int j=0;j<initValNode.getInitValNodes().get(i).getInitValNodes().size();j++){
                    Value tempValue=handleExp(initValNode.getInitValNodes().get(i).getInitValNodes().get(j).getExpNode());
                    //先把数组对应的地址取出来
                    Integer index=i*Integer.parseInt(store.twoarrayNum)+j;
                    Value pointer=buildFactory.createGetElementPtr(currentBasicBlock,store,new Const("0"),new Const(Integer.toString(index)));
                    buildFactory.createStoreInst(currentBasicBlock,tempValue,pointer);
                }
            }
        }
    }


    private List<String> handleConstInitVal(ConstInitValNode constInitValNode) {
    //ConstInitVal → ConstExp| '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        if (constInitValNode.getConstExpNode()==null){
            //说明接下里的是'{' [ ConstInitVal { ',' ConstInitVal } ] '}'
            //如果constinitval节点还是没有constexp，说明是二维的
            if (constInitValNode.getConstInitValNodes().get(0).getConstExpNode()==null){
                List<String> list=new ArrayList<>();
                for (ConstInitValNode node:constInitValNode.getConstInitValNodes()){
                    list.addAll(handleConstInitVal(node));
                }
                return list;
            }
            else{
                List<String> list=new ArrayList<>();
                for (ConstInitValNode node:constInitValNode.getConstInitValNodes()){
                    list.add(((Const)handleConstExp(node.getConstExpNode())).getName());
                }
                return list;
            }
        }
        return null;
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
    private Value zext(Value value){
        if (value.getType()!=ValueType.i32){
            User user=new User(buildFactory.getId(), ValueType.i32);
            buildFactory.createZextInst(currentBasicBlock,user,value,ValueType.i32);
            value.setType(ValueType.i32);
            return user;
        }
        return value;
    }
}
