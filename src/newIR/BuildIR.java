package newIR;

import Token.TokenType;
import newIR.Instruction.OpCode;
import newIR.Module.BasicBlock;
import newIR.Module.Function;
import newIR.Module.MyModule;
import newIR.ValueSon.Const;
import newIR.ValueSon.Global;
import newIR.ValueSon.User;
import newIR.ValueSon.Var;
import node.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuildIR {
    public MyModule module;
    public ValueTable currentValueTable;
    public BasicBlock currentBlock;
    private HashMap<String,Function> functionList= new HashMap<>();
    public BuildFactory buildFactory=BuildFactory.getBuildFactory();
    private Function currentFunction=null;

    private Value addBinaryInstruction(Value value1, Value value2, OpCode opCode) {
        if (value1 instanceof Const && value2 instanceof Const)
            return eval((Const) value1, (Const) value2, opCode);
        else {
            Value newValue1 = zext(value1);
            Value newValue2 = zext(value2);
            Value res=buildFactory.createBinaryInst(newValue1,newValue2,opCode);
            return res;
        }
    }

    private Value zext(Value value) {
        Value newValue = value;
        if (value.valueType != ValueType.i32) {
            newValue=buildFactory.createZextInst(currentBlock,value,ValueType.i32);
//            newValue = new User(getRegister(), ValueType.i32);
//            currentBasicBlock.appendInst(new ZextInstruction(value, ValueType.i32, newValue));
        }
        return newValue;
    }

    private Value eval(Const c1, Const c2, OpCode op) {
        if (op==OpCode.add)
            return new Const(Integer.toString(Integer.parseInt(c1.name) + Integer.parseInt(c2.name)));
        else if (op==OpCode.sub)
            return new Const(Integer.toString(Integer.parseInt(c1.name) - Integer.parseInt(c2.name)));
        else if (op==OpCode.mul)
            return new Const(Integer.toString(Integer.parseInt(c1.name) * Integer.parseInt(c2.name)));
        else if (op==OpCode.sdiv)
            return new Const(Integer.toString(Integer.parseInt(c1.name) / Integer.parseInt(c2.name)));
        else if (op==OpCode.srem)
            return new Const(Integer.toString(Integer.parseInt(c1.name) % Integer.parseInt(c2.name)));
        else return null;
    }

    public BuildIR(MyModule module) {
        this.module = module;
        this.currentValueTable=new ValueTable();
    }
    public void start(CompUnitNode node){
        //CompUnit → {Decl} {FuncDef} MainFuncDef
        Function getint=buildFactory.createFunction(module,"getint",ValueType.i32,false);
        Function putint=buildFactory.createFunction(module,"putint",ValueType.VOID,false);
        Function putch=buildFactory.createFunction(module,"putch",ValueType.VOID,false);
        functionList.put("getint",getint);
        functionList.put("putint",putint);
        functionList.put("putch",putch);
        for (DeclNode decl : node.getDeclNodes()){
            visitDecl(decl);
        }
        for (FuncDefNode f : node.getFuncDefNodes()){
            visitFuncDef(f);
        }
        visitMainFuncDef(node.getMainFuncDefNode());
    }

    private void visitFuncDef(FuncDefNode f) {
        // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        buildFactory.resetId0();
        String name=f.getIdent().getValue();
        Function function;
        if (f.getFuncTypeNode().getInttk()!=null) function=buildFactory.createFunction(module,name,ValueType.i32,true);
        else function=buildFactory.createFunction(module,name,ValueType.VOID,true);
        currentFunction=function;
        functionList.put(name,function);
        currentBlock=new BasicBlock();
        function.appendBlock(currentBlock);
        currentValueTable=currentValueTable.enterNextTbale();
        if (f.getFuncFParamsNode()!=null)   visitFuncFParams(f.getFuncFParamsNode(),function);
        else  currentBlock.setName(buildFactory.getId());
        for (BlockItemNode blockItemNode :f.getBlockNode().getBlockItemNodes()){
            visitBlockItem(blockItemNode);
        }
        currentValueTable=currentValueTable.father;
    }

    private void visitFuncFParams(FuncFParamsNode funcFParamsNode, Function function) {
        //FuncFParams → FuncFParam { ',' FuncFParam }
        for (FuncFParamNode funcFParamNode:funcFParamsNode.getFuncFParamsNodes()){
            Value param=visitFuncfParam(funcFParamNode);
            function.params.add(param);
        }
        currentBlock.setName(buildFactory.getId());
        int i=0;
        for (Value param:function.params){
            User alloc=buildFactory.createAllocateInst(currentBlock,param.valueType);
            if (param.twoarrayNum!=null) alloc.twoarrayNum= param.twoarrayNum;
            buildFactory.createStoreInst(currentBlock,param,alloc);
            currentValueTable.addValue(funcFParamsNode.getFuncFParamsNodes().get(i).getIdent().getValue(), alloc);
            i++;
        }
    }

    private Value visitFuncfParam(FuncFParamNode funcFParamNode) {
        //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        Var var = null;
        if (funcFParamNode.getBtypenode().getInttk()!=null){
            //传入的是普通变量
            if (funcFParamNode.getLbracks().size()==0){
                var=buildFactory.createVar(buildFactory.getId(),ValueType.i32,false);
                currentValueTable.addValue(funcFParamNode.getIdent().getValue(), var);
            }
            //传入的是一维数组
            else if (funcFParamNode.getLbracks().size()==1){
                var=buildFactory.createVar(buildFactory.getId(),ValueType.pointer,false);
                currentValueTable.addValue(funcFParamNode.getIdent().getValue(), var);
            }
            //传入的是二维数组
            else{
                var=buildFactory.createVar(buildFactory.getId(),ValueType.pointer,false);
                var.twoarrayNum=visitConstExp(funcFParamNode.getConstExpNodes().get(0)).constNum;
                currentValueTable.addValue(funcFParamNode.getIdent().getValue(), var);
            }
        }
        return var;
    }

    //这个decl是专门处理全局的变量的
    private void visitDecl(DeclNode decl) {
        //Decl → ConstDecl | VarDecl
        if (decl.getConstDeclNode()!=null) visitConstDecl(decl.getConstDeclNode());
        else visitVarDecl(decl.getVarDeclNode());
    }
    private void visitConstDecl(ConstDeclNode constDeclNode) {
        //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        ValueType type=visitBtype(constDeclNode.getbTypeNode());
        for (ConstDefNode f : constDeclNode.getConstDefNodes()){
            visitConstDef(f,type);
        }
    }

    private void visitConstDef(ConstDefNode f, ValueType type) {
        //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        String name=f.getIdent().getValue();
        Global global;
        //普通变量
        if (f.getConstExpNodes().size() == 0){
            global=buildFactory.createGlobal(module,name,ValueType.i32,true);
            Const constnum=visitConstExp(f.getConstInitValNode().getConstExpNode());
            global.num=Integer.parseInt(constnum.num);
        }//一位数组
        else if (f.getConstExpNodes().size() == 1) {
            global=buildFactory.createGlobal(module,name,ValueType.onearray,true);
            List<Value> initValues=visitConstInitalVal(f.getConstInitValNode());
            global.onearrayNum=Integer.parseInt(visitConstExp(f.getConstExpNodes().get(0)).num);
            global.arrayNums=initValues;
        }//二维数组
        else{
            global=buildFactory.createGlobal(module,name,ValueType.twoarray,true);
            global.onearrayNum=Integer.parseInt(visitConstExp(f.getConstExpNodes().get(0)).num);
            global.twoarrayNum=Integer.parseInt(visitConstExp(f.getConstExpNodes().get(1)).num);
            List<Value> initValues=visitConstInitalVal(f.getConstInitValNode());
            global.arrayNums=initValues;
        }
        currentValueTable.addValue(name,global);
    }
    private List<Value> visitConstInitalVal(ConstInitValNode constInitValNode) {
            //二维数组的初值
            if (constInitValNode.getConstInitValNodes().get(0).getConstExpNode()==null){
                List<Value> list=new ArrayList<>();
                for (ConstInitValNode node:constInitValNode.getConstInitValNodes()){
                    list.addAll(visitConstInitalVal(node));
                }
                return list;
            }
            else{
                List<Value> list=new ArrayList<>();
                for (ConstInitValNode node:constInitValNode.getConstInitValNodes()){
                    list.add(new Const(visitConstExp(node.getConstExpNode()).num));
                }
                return list;
            }
    }
    private void visitVarDecl(VarDeclNode varDeclNode) {
        ValueType type = visitBtype(varDeclNode.getbTypeNode());
        for (VarDefNode varDef : varDeclNode.getVarDefNodes()){
            visitVarDef(varDef,type);
        }
    }

    private void visitVarDef(VarDefNode varDef, ValueType type) {
// VarDef → Ident { '[' ConstExp ']' } |Ident { '[' ConstExp ']' } '=' InitVal
        String name = varDef.getIdent().getValue();
        Global global;
        //普通变量
        if (varDef.getConstExpNodes().size() == 0){
            global=buildFactory.createGlobal(module,name,ValueType.i32,true);
            if (varDef.getInitValNode()!=null){
                Const constnum=(Const)visitExp(varDef.getInitValNode().getExpNode());
                global.num=Integer.parseInt(constnum.num);
            }
            else global.num=0;

        }//一位数组
        else if (varDef.getConstExpNodes().size() == 1) {
            global=buildFactory.createGlobal(module,name,ValueType.onearray,true);
            global.onearrayNum=Integer.parseInt(visitConstExp(varDef.getConstExpNodes().get(0)).num);
            if (varDef.getInitValNode()!=null){
                List<Value> initValues=visitInitVal(varDef.getInitValNode());
                global.arrayNums=initValues;
            }
        }//二维数组
        else{
            global=buildFactory.createGlobal(module,name,ValueType.twoarray,true);
            global.onearrayNum=Integer.parseInt(visitConstExp(varDef.getConstExpNodes().get(0)).num);
            global.twoarrayNum=Integer.parseInt(visitConstExp(varDef.getConstExpNodes().get(1)).num);
            if (varDef.getInitValNode() != null) {
                List<Value> initValues = visitInitVal(varDef.getInitValNode());
                global.arrayNums = initValues;
            }
        }
        currentValueTable.addValue(name,global);
    }
    private Const visitConstExp(ConstExpNode constExpNode) {
        return (Const) visitAddExp(constExpNode.getAddExpNode());
    }

    private Value visitAddExp(AddExpNode addExpNode) {
        if (addExpNode.getMulExpNodes().size()==1){
            return visitMulExp(addExpNode.getMulExpNodes().get(0));
        }
        else{
            Value value1=visitMulExp(addExpNode.getMulExpNodes().get(0));
            Value value2=visitMulExp(addExpNode.getMulExpNodes().get(1));
            Value user=addBinaryInstruction(value1,value2,addExpNode.getPluseOrminus().get(0).getType()== TokenType.PLUS ? OpCode.add: OpCode.sub);
            for (int i=2;i<addExpNode.getMulExpNodes().size(); i++){
                Value tempValue=visitMulExp(addExpNode.getMulExpNodes().get(i));
                user=addBinaryInstruction(user,tempValue,addExpNode.getPluseOrminus().get(i-1).getType()==TokenType.PLUS ? OpCode.add: OpCode.sub);
            }
            return user;
        }
    }

    private Value visitMulExp(MulExpNode mulExpNode) {
        if (mulExpNode.getUnaryExpNodes().size()==1){
            return visitUnaryExp(mulExpNode.getUnaryExpNodes().get(0));
        }
        else{
            Value value1=visitUnaryExp(mulExpNode.getUnaryExpNodes().get(0));
            Value value2=visitUnaryExp(mulExpNode.getUnaryExpNodes().get(1));
            OpCode op=OpCode.Token2Op(mulExpNode.getOps().get(0).getType());
            Value user=addBinaryInstruction(value1,value2,op);
            for (int i=2;i<mulExpNode.getUnaryExpNodes().size();i++){
                Value tempValue=visitUnaryExp(mulExpNode.getUnaryExpNodes().get(i));
                op=OpCode.Token2Op(mulExpNode.getOps().get(i-1).getType());
                user=addBinaryInstruction(user,tempValue,op);
            }
            return user;
        }
    }

    private Value visitUnaryExp(UnaryExpNode unaryExpNode) {
        //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        if (unaryExpNode.getPrimaryExpNode()!=null){
            return visitPrimaryExp(unaryExpNode.getPrimaryExpNode());
        } else if (unaryExpNode.getIdent()!=null) {
            Function function=functionList.get(unaryExpNode.getIdent().getValue());
            List<Value> params=new ArrayList<>();
            if (unaryExpNode.getFuncRParamsNode() != null){
                for (ExpNode expNode : unaryExpNode.getFuncRParamsNode().getExpNodes()){
                    Value value =visitExp(expNode);
                    params.add(value);
                }
            }
            User user=buildFactory.createCallInst(currentBlock,function,params);
            return user;
        }
        else{
            if (unaryExpNode.getUnaryOpNode().getMinu()!=null) {
                Value value1=new Const("0");
                Value value2=visitUnaryExp(unaryExpNode.getUnaryExpNode());
                Value user=addBinaryInstruction(value1,value2,OpCode.sub);
                return user;
            }
            else if(unaryExpNode.getUnaryOpNode().getPlus() != null){
                return visitUnaryExp(unaryExpNode.getUnaryExpNode());
            }
            //处理非的情况
            else{
                Value value1=visitUnaryExp(unaryExpNode.getUnaryExpNode());
                User user=buildFactory.createIcmp(currentBlock,value1,new Const("0"),OpCode.eq);
                return user;
            }
        }
    }

    private Value visitPrimaryExp(PrimaryExpNode primaryExpNode) {
        if (primaryExpNode.getNumberNode() != null){
            return visitNumber(primaryExpNode.getNumberNode());
        }
        else if (primaryExpNode.getlValNode() != null) {
            return visitLval(primaryExpNode.getlValNode());
        }
        else{
            return visitExp(primaryExpNode.getExpNode());
        }
    }

    private Value visitLval(LValNode getlValNode) {
        //LVal → Ident {'[' Exp ']'}
        Value value=currentValueTable.searchValue(getlValNode.getIdent().getValue());
        if (value.valueType==ValueType.i32){
            if (value.isConst){
                return new Const(value.constNum.toString());
            }
            return buildFactory.createLoadInst(currentBlock,value);
        }
        else if (value.valueType==ValueType.onearray){
            //说明要从一维数组中取一个int32
            if (getlValNode.getExpNodes().size()==1){
                Value index1=visitExp(getlValNode.getExpNodes().get(0));
                if (value.isConst) {
                    if (index1.isConst){
                        return new Const(value.arrayNum.get(index1.constNum).toString());
                    }
                }
                User tempUser=buildFactory.createGetElementPtr(currentBlock,value,new Const("0"),index1);
                User user=buildFactory.createLoadInst(currentBlock,tempUser);
                return user;
            }
            //否则就是取一维数组的地址
            User user=buildFactory.createGetElementPtr(currentBlock,value,new Const("0"),new Const("0"));
            return user;
        }
        else if (value.valueType==ValueType.twoarray){
            if (getlValNode.getExpNodes().size()==2){
                Value index1=visitExp(getlValNode.getExpNodes().get(0));
                Value index2=visitExp(getlValNode.getExpNodes().get(1));
                if (value.isConst){
                    if (index1.isConst&&index2.isConst){
                        return new Const(value.arrayNum.get(index1.constNum* value.twoarrayNum+index2.constNum).toString());
                    }
                }
                Value temp=addBinaryInstruction(index1,new Const(value.twoarrayNum.toString()),OpCode.mul);
                Value sum=addBinaryInstruction(temp,index2,OpCode.add);
                User tempUser=buildFactory.createGetElementPtr(currentBlock,value,new Const("0"),sum);
                User user=buildFactory.createLoadInst(currentBlock,tempUser);
                return user;
            }//取二维数组的一维地址
            else if (getlValNode.getExpNodes().size()==1){
                Value index1=visitExp(getlValNode.getExpNodes().get(0));
                Value sum=addBinaryInstruction(index1,new Const(value.twoarrayNum.toString()),OpCode.mul);
                User tempUser=buildFactory.createGetElementPtr(currentBlock,value,new Const("0"),sum);
                return tempUser;
            }
            else{
                User tempUser=buildFactory.createGetElementPtr(currentBlock,value,new Const("0"),new Const("0"));
                return tempUser;
            }
        }//从一个指针中取
        else{
            if (getlValNode.getExpNodes().size()==0){
                User user=buildFactory.createLoadInst(currentBlock,value);
                return user;
            }
            else if (getlValNode.getExpNodes().size()==1){
                //如果ident是二维，exp只有一个，说明传入的是一维
                if (value.twoarrayNum!=null){
                    User tempuser=buildFactory.createLoadInst(currentBlock,value);
                    Value index1=visitExp(getlValNode.getExpNodes().get(0));
                    Value sum=addBinaryInstruction(index1,new Const(value.twoarrayNum.toString()),OpCode.mul);
                    User user=buildFactory.createGetElementPtr(currentBlock,value,new Const("0"),sum);
                    return user;
                }
                else{
                    User tempuser=buildFactory.createLoadInst(currentBlock,value);
                    Value index1=visitExp(getlValNode.getExpNodes().get(0));
                    User addr=buildFactory.createGetElementPtr(currentBlock,value,new Const("0"),index1);
                    User user =buildFactory.createLoadInst(currentBlock,addr);
                    return user;
                }

            }
            else {
                User tempuser=buildFactory.createLoadInst(currentBlock,value);
                Value index1=visitExp(getlValNode.getExpNodes().get(0));
                Value index2=visitExp(getlValNode.getExpNodes().get(1));
                Value temp=addBinaryInstruction(index1,new Const(value.twoarrayNum.toString()),OpCode.mul);
                Value sum=addBinaryInstruction(temp,index2,OpCode.add);
                User addr=buildFactory.createGetElementPtr(currentBlock,value,new Const("0"),sum);
                User user=buildFactory.createLoadInst(currentBlock,addr);
                return user;
            }
        }
    }
    private Value visitLvalLeft(LValNode lValNode){
        Value value=currentValueTable.searchValue(lValNode.getIdent().getValue());
        if (value.valueType== ValueType.i32){
            return value;
        }
        //LVal → Ident {'[' Exp ']'}'
        else if (value.valueType==ValueType.onearray){
            Value value1=visitExp(lValNode.getExpNodes().get(0));
            Value user=buildFactory.createGetElementPtr(currentBlock,value,new Const("0"),value1);
            return user;
        }
        else if (value.valueType== ValueType.twoarray){
            Value value1=visitExp(lValNode.getExpNodes().get(0));
            Value value2=visitExp(lValNode.getExpNodes().get(1));
            Value temp=addBinaryInstruction(value1,new Const(value.twoarrayNum.toString()), OpCode.mul);
            Value index=addBinaryInstruction(temp,value2, OpCode.add);
            Value user=buildFactory.createGetElementPtr(currentBlock,value,new Const("0"),index);
            return user;
        }

        else if (value.valueType== ValueType.pointer){
            if (lValNode.getExpNodes().size()==1){
                Value value1=visitExp(lValNode.getExpNodes().get(0));
                User addr=buildFactory.createLoadInst(currentBlock,value);
                User user=buildFactory.createGetElementPtr(currentBlock,addr,value1);
                return user;
            }
            else{
                Value value1=visitExp(lValNode.getExpNodes().get(0));
                Value value2=visitExp(lValNode.getExpNodes().get(1));
                Value temp=addBinaryInstruction(value1,new Const(value.twoarrayNum.toString()), OpCode.mul);
                Value index=addBinaryInstruction(temp,value2, OpCode.add);
                User tempuser=buildFactory.createLoadInst(currentBlock,value);
                Value user=buildFactory.createGetElementPtr(currentBlock,tempuser,index);
                return user;
            }
        }
        else{
            return null;
        }
    }
    private Value visitNumber(NumberNode numberNode) {
        return  new Const(numberNode.getNumber().getValue());
    }


    private Value visitExp(ExpNode expNode) {
        return visitAddExp(expNode.getAddExpNode());
    }
    //这个是处理全局变量的
    private List<Value> visitInitVal(InitValNode initValNode) {
        if (initValNode.getInitValNodes().get(0).getExpNode()==null){
            List<Value> list=new ArrayList<>();
            for (InitValNode node:initValNode.getInitValNodes()){
                list.addAll(visitInitVal(node));
            }
            return list;
        }
        else{
            List<Value> list=new ArrayList<>();
            for (InitValNode node:initValNode.getInitValNodes()){
                list.add(new Const(((Const)visitExp(node.getExpNode())).num));
            }
            return list;
        }
    }
    //这个是处理局部变量的


    private void visitMainFuncDef(MainFuncDefNode mainFuncDefNode) {
        //MainFuncDef → 'int' 'main' '(' ')' Block
        Function function=buildFactory.createFunction(module,"main",ValueType.i32,true);
        currentFunction=function;
        buildFactory.resetId0();
        BasicBlock block=new BasicBlock();
        block.setName(buildFactory.getId());
        currentBlock=currentBlock.enterNextBlock();
        function.appendBlock(currentBlock);
        currentValueTable=currentValueTable.enterNextTbale();
        for (BlockItemNode blockItemNode :mainFuncDefNode.getBlockNode().getBlockItemNodes()){
            visitBlockItem(blockItemNode);
        }
    }

    private void visitBlockItem(BlockItemNode blockItemNode) {
        //BlockItem → Decl | Stmt
        if (blockItemNode.getDeclnode()!=null)visitDecl(currentValueTable, blockItemNode.getDeclnode());
        else visitStmt(blockItemNode.getStmtnode());
    }


//这里的变量处理都是针对局部变量的
    private void visitDecl(ValueTable currentValueTable, DeclNode declnode) {
        // Decl → ConstDecl | VarDecl
        if (declnode.getConstDeclNode()!=null)visitConstDecl(currentValueTable, declnode.getConstDeclNode());
        else visitVarDecl(currentValueTable, declnode.getVarDeclNode());
    }

    private void visitConstDecl(ValueTable currentValueTable, ConstDeclNode constDeclNode) {
        //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        ValueType type = visitBtype(constDeclNode.getbTypeNode());
        for (ConstDefNode constDef : constDeclNode.getConstDefNodes()){
            visitConstDef(currentValueTable,constDef,type);
        }

    }

    private void visitConstDef(ValueTable currentValueTable, ConstDefNode constDef, ValueType type) {
        //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        String name=constDef.getIdent().getValue();
        User var;
        if (constDef.getConstExpNodes().size()==0){
            var=buildFactory.createVar(currentBlock,name,ValueType.i32,true);
            var.constNum=Integer.parseInt(visitConstExp(constDef.getConstInitValNode().getConstExpNode()).num);
            visitConstInitVal2(var,constDef.getConstInitValNode());
        }
        else if (constDef.getConstExpNodes().size()==1){
            var=buildFactory.createVar(currentBlock,name,ValueType.onearray,true);
            List<Value> initValues=visitConstInitalVal(constDef.getConstInitValNode());
            var.onearrayNum=Integer.parseInt(visitConstExp(constDef.getConstExpNodes().get(0)).num);
            var.arrayNum=initValues;
            visitConstInitVal2(var,constDef.getConstInitValNode());
        }
        else{
            var=buildFactory.createVar(currentBlock,name,ValueType.twoarray,true);
            var.onearrayNum=Integer.parseInt(visitConstExp(constDef.getConstExpNodes().get(0)).num);
            var.twoarrayNum=Integer.parseInt(visitConstExp(constDef.getConstExpNodes().get(1)).num);
            List<Value> initValues=visitConstInitalVal(constDef.getConstInitValNode());
            var.arrayNum=initValues;
            visitConstInitVal2(var,constDef.getConstInitValNode());
        }
        currentValueTable.addValue(name,var);
    }

    private void visitVarDecl(ValueTable currentValueTable, VarDeclNode varDeclNode) {
       // VarDecl → BType VarDef { ',' VarDef } ';'
        ValueType type = visitBtype(varDeclNode.getbTypeNode());
        for (VarDefNode varDef : varDeclNode.getVarDefNodes()){
            visitVarDef(currentValueTable,varDef,type);
        }
    }

    private void visitVarDef(ValueTable currentValueTable, VarDefNode varDef, ValueType type) {
        // VarDef → Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
        String name = varDef.getIdent().getValue();
        User var;
        if (varDef.getConstExpNodes().size() == 0){
            var=buildFactory.createVar(currentBlock,name, ValueType.i32,false);
            if (varDef.getInitValNode() != null){
                visitInitVal(var,varDef.getInitValNode());
            }

        }
        else if (varDef.getConstExpNodes().size() == 1){
            var=buildFactory.createVar(currentBlock,name, ValueType.onearray,false);
            var.onearrayNum=Integer.parseInt(visitConstExp(varDef.getConstExpNodes().get(0)).num);
            if (varDef.getInitValNode() != null){
                visitInitVal(var,varDef.getInitValNode());
            }
        }
        else {
            var=buildFactory.createVar(currentBlock,name, ValueType.twoarray,false);
            var.onearrayNum=Integer.parseInt(visitConstExp(varDef.getConstExpNodes().get(0)).num);
            var.twoarrayNum=Integer.parseInt(visitConstExp(varDef.getConstExpNodes().get(1)).num);
            if (varDef.getInitValNode() != null){
                visitInitVal(var,varDef.getInitValNode());
            }
        }
        currentValueTable.addValue(name,var);
    }
    private void visitConstInitVal2(Value varReg, ConstInitValNode initValNode) {
        // ConstInitVal → ConstExp|'{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        if (initValNode.getConstExpNode()!=null){
            Value initVal= visitConstExp(initValNode.getConstExpNode());
            buildFactory.createStoreInst(currentBlock,initVal,varReg);
        }
        else if (varReg.valueType==ValueType.onearray){
            for (int i=0;i<initValNode.getConstInitValNodes().size();i++){
                Value tempValue=visitConstExp(initValNode.getConstInitValNodes().get(i).getConstExpNode());
                //先把数组对应的地址取出来
                Value pointer=buildFactory.createGetElementPtr(currentBlock,varReg,new Const("0"),new Const(Integer.toString(i)));
                buildFactory.createStoreInst(currentBlock,tempValue,pointer);
            }
        }
        //二维数组
        else{
            for (int i=0;i<initValNode.getConstInitValNodes().size();i++){
                for (int j=0;j<initValNode.getConstInitValNodes().get(i).getConstInitValNodes().size();j++){
                    Value tempValue=visitConstExp(initValNode.getConstInitValNodes().get(i).getConstInitValNodes().get(j).getConstExpNode());
                    //先把数组对应的地址取出来
                    Integer index=i*Integer.parseInt(String.valueOf(varReg.twoarrayNum))+j;
                    Value pointer=buildFactory.createGetElementPtr(currentBlock,varReg,new Const("0"),new Const(Integer.toString(index)));
                    buildFactory.createStoreInst(currentBlock,tempValue,pointer);
                }
            }
        }
    }
    private void visitInitVal(Value varReg, InitValNode initValNode) {
        // InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        if (initValNode.getExpNode()!=null){
            Value initVal= visitExp(initValNode.getExpNode());
            buildFactory.createStoreInst(currentBlock,initVal,varReg);
        }
        else if (varReg.valueType==ValueType.onearray){
            for (int i=0;i<initValNode.getInitValNodes().size();i++){
                Value tempValue=visitExp(initValNode.getInitValNodes().get(i).getExpNode());
                //先把数组对应的地址取出来
                Value pointer=buildFactory.createGetElementPtr(currentBlock,varReg,new Const("0"),new Const(Integer.toString(i)));
                buildFactory.createStoreInst(currentBlock,tempValue,pointer);
            }
        }
        //二维数组
        else{
            for (int i=0;i<initValNode.getInitValNodes().size();i++){
                for (int j=0;j<initValNode.getInitValNodes().get(i).getInitValNodes().size();j++){
                    Value tempValue=visitExp(initValNode.getInitValNodes().get(i).getInitValNodes().get(j).getExpNode());
                    //先把数组对应的地址取出来
                    Integer index=i*Integer.parseInt(String.valueOf(varReg.twoarrayNum))+j;
                    Value pointer=buildFactory.createGetElementPtr(currentBlock,varReg,new Const("0"),new Const(Integer.toString(index)));
                    buildFactory.createStoreInst(currentBlock,tempValue,pointer);
                }
            }
        }
    }

    private void visitStmt(StmtNode stmtNode) {
        //LVal '=' Exp ';'
        if (stmtNode.getStmtType()== StmtNode.StmtType.LvalAssignExp){
            Value value=visitLvalLeft(stmtNode.getLvalnode());
            Value exp=visitExp(stmtNode.getExpNode());
            buildFactory.createStoreInst(currentBlock,exp,value);
        }//| [Exp] ';'
        else if (stmtNode.getStmtType()== StmtNode.StmtType.Exp){
            if (stmtNode.getExpNode()!=null){
                visitExp(stmtNode.getExpNode());
            }
        }//| Block
        else if (stmtNode.getStmtType()== StmtNode.StmtType.Block){
            currentValueTable.enterNextTbale();
            for (BlockItemNode blockItemNode :stmtNode.getBlockNode().getBlockItemNodes()){
                visitBlockItem(blockItemNode);
            }
            currentValueTable=currentValueTable.father;
        }//| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        else if (stmtNode.getStmtType()== StmtNode.StmtType.If){
            BasicBlock ifblock=new BasicBlock();
            BasicBlock outblock=new BasicBlock();
            BasicBlock elseblock=null;
            currentFunction.appendBlock(ifblock);
            if (stmtNode.getElsetk() != null) {
                elseblock=new BasicBlock();
            }
            visitCond(stmtNode.getCondNode(),ifblock,elseblock,outblock);
            //处理ifblock
            ifblock.setName(buildFactory.getId());
            currentBlock=currentBlock.enterNextBlock(ifblock);
            visitStmt(stmtNode.getStmtNodes().get(0));
            //如果有else
            if (stmtNode.getElsetk()!=null){
                elseblock.setName(buildFactory.getId());
                currentBlock=currentBlock.enterNextBlock(elseblock);
                visitStmt(stmtNode.getStmtNodes().get(1));
                currentFunction.appendBlock(elseblock);
            }
            outblock.setName(buildFactory.getId());
            currentBlock.enterNextBlock(outblock);
            currentFunction.appendBlock(outblock);
        }//| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
        else if () {

        } //| 'break' ';'
        else if (stmtNode.getStmtType()== StmtNode.StmtType.Break){
            //buildFactory.createBrInst(currentBlock,currentBlock.breakBlock);
        }//| 'continue' ';'
        else if (stmtNode.getStmtType()== StmtNode.StmtType.Continue) {
           // buildFactory.createBrInst(currentBlock, currentBlock.continueBlock);
        }//| 'return' [Exp] ';'
        else if (stmtNode.getStmtType()== StmtNode.StmtType.Return){
            if (stmtNode.getExpNode()!=null) {
                Value value=visitExp(stmtNode.getExpNode());
                buildFactory.createRetInst(currentBlock,value,ValueType.i32);
            }
            else{
                buildFactory.createRetInst(currentBlock,new Value(),ValueType.VOID);
            }
        }
        //| LVal '=' 'getint''('')'';'
        else if (stmtNode.getStmtType()== StmtNode.StmtType.LvalAssignGetint) {
            Value value=visitLvalLeft(stmtNode.getLvalnode());
            User user=buildFactory.createCallInst(currentBlock,functionList.get("getint"),new ArrayList<>());
            buildFactory.createStoreInst(currentBlock,user,value);
        }
        //| 'printf''('FormatString{','Exp}')'';'
        else{

        }
    }

    private void visitCond(CondNode condNode, BasicBlock ifblock, BasicBlock elseblock, BasicBlock outblock) {
        visitLOrExp(condNode.getlOrExpNode(), ifblock, elseblock, outblock);
    }

    private void visitLOrExp(LOrExpNode lOrExpNode, BasicBlock ifblock, BasicBlock elseblock, BasicBlock outblock) {
        //LOrExp → LAndExp {'||' LAndExp}
        for (int i=0;i<lOrExpNode.getlAndExpNodes().size();i++) {
            BasicBlock judge=null;
            if (i!=lOrExpNode.getlAndExpNodes().size()-1) {
                judge=new BasicBlock();
            }
            Value value=visitLandExp(lOrExpNode.getlAndExpNodes().get(i),ifblock,elseblock,outblock,judge);
            BasicBlock falseBlock=null;
            if (value.valueType== ValueType.i32){
                User user=buildFactory.createIcmp(currentBlock,value,new Const("0"), OpCode.ne);
                value=user;
            }
            //如果这是最后一个或判断
            if (i==lOrExpNode.getlAndExpNodes().size()-1){
                falseBlock=(elseblock==null?outblock:elseblock);
            }
            else{
                judge.setName(buildFactory.getId());
                falseBlock=judge;
                currentFunction.appendBlock(judge);
            }
            if (!(lOrExpNode.getlAndExpNodes().get(i).getEqExpNodes().size()>1)) {
                buildFactory.createBranchInst(currentBlock,value,ifblock,falseBlock);
            }
            if (i!=lOrExpNode.getlAndExpNodes().size()-1) currentBlock=currentBlock.enterNextBlock(falseBlock);
        }
    }

    private Value visitLandExp(LAndExpNode lAndExpNode, BasicBlock ifblock, BasicBlock elseblock, BasicBlock outblock, BasicBlock nextblock) {
        //LAndExp → EqExp { '&&' EqExp}
        Value value=null;
        if(lAndExpNode.getEqExpNodes().size()==1) return visitEqExp(lAndExpNode.getEqExpNodes().get(0));
        for (int i=0;i<lAndExpNode.getEqExpNodes().size();i++) {
            value=visitEqExp(lAndExpNode.getEqExpNodes().get(i));
            if (value.valueType== ValueType.i32){
                User user =buildFactory.createIcmp(currentBlock,value,new Const("0"), OpCode.ne);
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
                currentFunction.appendBlock(judge);
                //如果接下来没有或判断，那么这里出错就直接去else或者out
                if (nextblock == null){
                    falseblock=elseblock==null?outblock : elseblock;
                }
                else{
                    falseblock=nextblock;
                }
            }
            buildFactory.createBranchInst(currentBlock,value,trueblock,falseblock);
            if (i!=lAndExpNode.getEqExpNodes().size()-1)currentBlock=currentBlock.enterNextBlock(trueblock);
        }
        return value;
    }

    private Value visitEqExp(EqExpNode eqExpNode) {
        if (eqExpNode.getRelExpNodes().size()==1) return visitRelExp(eqExpNode.getRelExpNodes().get(0));
        else{
            Value value1=zext(visitRelExp(eqExpNode.getRelExpNodes().get(0)));
            Value value2=zext(visitRelExp(eqExpNode.getRelExpNodes().get(1)));
            OpCode opcode= OpCode.Token2Op(eqExpNode.getEqlOrNeqs().get(0).getType());
            User user=buildFactory.createIcmp(currentBlock,value1,value2,opcode);
            for (int i=2;i<eqExpNode.getRelExpNodes().size();i++){
                value1=zext(user);
                value2=visitRelExp(eqExpNode.getRelExpNodes().get(i));
                opcode= OpCode.Token2Op(eqExpNode.getEqlOrNeqs().get(i-1).getType());
                user= buildFactory.createIcmp(currentBlock,value1,value2,opcode);
            }
            return user;
        }
    }

    private Value visitRelExp(RelExpNode relExpNode) {
        if (relExpNode.getAddExpNodes().size()==1) return visitAddExp(relExpNode.getAddExpNodes().get(0));
        else{
            Value value1=zext(visitAddExp(relExpNode.getAddExpNodes().get(0)));
            Value value2=zext(visitAddExp(relExpNode.getAddExpNodes().get(1)));
            OpCode opcode= OpCode.Token2Op(relExpNode.getOps().get(0).getType());
            User user =buildFactory.createIcmp(currentBlock,value1,value2,opcode);
            for (int i=2;i<relExpNode.getAddExpNodes().size();i++){
                value1=zext(user);
                value2=visitAddExp(relExpNode.getAddExpNodes().get(i));
                opcode= OpCode.Token2Op(relExpNode.getOps().get(i-1).getType());
                user= buildFactory.createIcmp(currentBlock,value1,value2,opcode);
            }
            return user;
        }
    }

    private ValueType visitBtype(BTypeNode getbTypeNode) {
        if (getbTypeNode.getInttk()!=null){
            return ValueType.i32;
        }
        return null;
    }
}
