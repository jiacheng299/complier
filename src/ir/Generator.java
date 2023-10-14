package ir;

import Token.TokenType;
import ir.Basic.BasicBlock;
import ir.Basic.Const;
import ir.Basic.Function;
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
    private static Integer index=1;
    private StringBuilder irCode=new StringBuilder();
    private Module module=new Module();
    private Value tmpValue=null;
    private BuildFactory buildFactory = BuildFactory.getBuildFactory();
    private static Generator generator=new Generator();
    private BasicBlock currentBasicBlock=null;
    public void Generator(){}
    public Value addBinaryInstruction(Value value1, Value value2, OpCode op) {
        if (value1 instanceof Const && value2 instanceof Const){
            if (op==OpCode.add){
                return buildFactory.createConst(Integer.toString(Integer.parseInt(value1.getName())+Integer.parseInt(value2.getName())));
            }
            else if (op == OpCode.sub) {
                return buildFactory.createConst(Integer.toString(Integer.parseInt(value1.getName())-Integer.parseInt(value2.getName())));
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

        handleMainFunc(compUnitNode.getMainFuncDefNode());
       // System.out.println(irCode);
    }

    private void handleMainFunc(MainFuncDefNode mainFuncDefNode) {
       Function function=new Function("main", DataType.i32);
       module.addFunction(function);
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
    }

}
