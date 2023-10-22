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

public class Generator {
    private SymbolTableNode currentNode=SymbolTableNode.getCurrentNode();
    private SymbolInfo currentFunction=null;
    private SymbolInfo returnNum=null;
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
        if (value1 instanceof Const && value2 instanceof Const){
            if (op==OpCode.add){
                return buildFactory.createConst(Integer.toString(Integer.parseInt(value1.getName())+Integer.parseInt(value2.getName())));
            }
            else if (op == OpCode.sub) {
                User user = new User(buildFactory.getId(),ValueType.i32);
               // buildFactory.createBinaryInst(currentBasicBlock,value1,value2,user,OpCode.sub);
                return buildFactory.createConst(Integer.toString(Integer.parseInt(value1.getName())-Integer.parseInt(value2.getName())));
//                return user;
            }
            else if (op == OpCode.mul) {
                return buildFactory.createConst(Integer.toString(Integer.parseInt(value1.getName())*Integer.parseInt(value2.getName())));
            }
            else if (op == OpCode.mod){
                return buildFactory.createConst(Integer.toString(Integer.parseInt(value1.getName())%Integer.parseInt(value2.getName())));
            }
            else{
                return buildFactory.createConst(Integer.toString(Integer.parseInt(value1.getName())/Integer.parseInt(value2.getName())));
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
       Function function=new Function("main", DataType.i32);
       currentModule.addFunction(function);
       BasicBlock block=new BasicBlock();
       currentBasicBlock=block;
       function.addBasicBlock(block);
       handleBlock(mainFuncDefNode.getBlockNode());
    }

    private void handleBlock(BlockNode blockNode) {
        for (BlockItemNode blockItem : blockNode.getBlockItemNodes()){
            handleBlockItem(blockItem);
        }
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
            return null;
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
        if (currentValueTable.father==null){
            if (varDefNode.getConstExpNodes().size()==0){
                GlobalVar globalVar = null;
                if (bTypeNode.getInttk()!=null) {
                    globalVar=buildFactory.createGlobalVar(varDefNode.getIdent().getValue(), ValueType.i32, false);
                }
                currentValueTable.addValue(varDefNode.getIdent().getValue(),globalVar);
                int num=0;
                if (varDefNode.getInitValNode()!=null){
                    num = ((Const)handleExp(varDefNode.getInitValNode().getExpNode())).getValue();
                }
                globalVar.setNum(num);
                currentValueTable.addValue(varDefNode.getIdent().getValue(),globalVar);
            }
        }
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
