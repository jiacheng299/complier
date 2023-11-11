package newIR;

import Token.TokenType;
import newIR.Instruction.CallInstruction;
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
        for (DeclNode decl : node.getDeclNodes()){
            visitDecl(decl);
        }
        for (FuncDefNode f : node.getFuncDefNodes()){
           // visitFuncDef(f);
        }
        visitMainFuncDef(node.getMainFuncDefNode());
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
            CallInstruction callInstruction=null;
            User user=null;
            List<Value> params=new ArrayList<>();
            if (unaryExpNode.getFuncRParamsNode() != null){
                for (ExpNode expNode : unaryExpNode.getFuncRParamsNode().getExpNodes()){
                    Value value =visitExp(expNode);
                    params.add(value);
                }
            }
            callInstruction=buildFactory.createCallInst(currentBlock,function);
//            if (function.returnType!=ValueType.VOID){
//                user=new User(buildFactory.getId(), function.returnType);
//                callInstruction=buildFactory.createCallInst(currentBasicBlock,function,user);
//
//            }
//            else{
//                callInstruction = buildFactory.createCallInst(currentBasicBlock,function);
//            }
            for (Value value:params) callInstruction.addParam(value);
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

            }
            //否则就是取一维数组的地址
        }
        else if (value.valueType==ValueType.twoarray){

        }//从一个指针中取
        else{

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
        for (BlockItemNode blockItemNode :mainFuncDefNode.getBlockNode().getBlockItemNodes()){
            visitBlockItem(blockItemNode);
        }
    }

    private void visitBlockItem(BlockItemNode blockItemNode) {
        //BlockItem → Decl | Stmt
        if (blockItemNode.getDeclnode()!=null)visitDecl(currentValueTable, blockItemNode.getDeclnode());
        else visitStmt(blockItemNode);
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
        }
        else if (constDef.getConstExpNodes().size()==1){
            var=buildFactory.createVar(currentBlock,name,ValueType.onearray,true);
            List<Value> initValues=visitConstInitalVal(constDef.getConstInitValNode());
            var.onearrayNum=Integer.parseInt(visitConstExp(constDef.getConstExpNodes().get(0)).num);
            var.arrayNum=initValues;
        }
        else{
            var=buildFactory.createVar(currentBlock,name,ValueType.twoarray,true);
            var.onearrayNum=Integer.parseInt(visitConstExp(constDef.getConstExpNodes().get(0)).num);
            var.twoarrayNum=Integer.parseInt(visitConstExp(constDef.getConstExpNodes().get(1)).num);
            List<Value> initValues=visitConstInitalVal(constDef.getConstInitValNode());
            var.arrayNum=initValues;
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

    private void visitStmt(BlockItemNode blockItemNode) {
    }
    private ValueType visitBtype(BTypeNode getbTypeNode) {
        if (getbTypeNode.getInttk()!=null){
            return ValueType.i32;
        }
        return null;
    }
}
