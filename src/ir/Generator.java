package ir;

import Token.TokenType;
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
    public void start(CompUnitNode compUnitNode){

        handleMainFunc(compUnitNode.getMainFuncDefNode());
       // System.out.println(irCode);
    }

    private void handleMainFunc(MainFuncDefNode mainFuncDefNode) {
       Function function=new Function("main",Function.ReturnType.i32);
       module.addFunction(function);
       BasicBlock block=new BasicBlock();
       currentBasicBlock=block;
       function.addBasicBlock(block);
       handleBlock(mainFuncDefNode.getBlockNode());
    }

    private void handleBlock(BlockNode blockNode) {
        for (BlockItemNode blockItem:blockNode.getBlockItemNodes()){
            handleBlockItem(blockItem);
        }
    }

    private void handleBlockItem(BlockItemNode blockItem) {
        if(blockItem.getStmtnode()!=null){
            handleStmt(blockItem.getStmtnode());
        }
    }

    private void handleStmt(StmtNode stmtnode) {
        //Stmt        → 'return' [Exp] ';'
        if (stmtnode.getReturntk() != null){
            if (stmtnode.getExpNode()!=null) {
                handleExp(stmtnode.getExpNode());
                buildFactory.createRetInst(index,currentBasicBlock,tmpValue);
            }
            else  {
                buildFactory.createRetInst(index,currentBasicBlock);
            }
        }
    }

    private void handleExp(ExpNode expNode) {
        tmpValue=null;
        handleAddExp(expNode.getAddExpNode());
    }

    private void handleAddExp(AddExpNode addExpNode) {
        //AddExp     → MulExp {('+' | '−') MulExp}
        handleMulExp(addExpNode.getMulExpNodes().get(0));
        for (int i = 0; i < addExpNode.getPluseOrminus().size(); i++){

            handleMulExp(addExpNode.getMulExpNodes().get(i+1));
            if (addExpNode.getPluseOrminus().get(i).getType()== TokenType.PLUS){
                buildFactory.createInst(OpCode.add,index,new Value(),new Value(),currentBasicBlock);
                irCode.append("%"+index+" = add i32 \n");
            }
            else{
                buildFactory.createInst(OpCode.sub,index,new Value(),new Value(),currentBasicBlock);
                irCode.append("%"+index+" = sub i32 \n");
            }
            index++;
        }
    }

    private void handleMulExp(MulExpNode mulExpNode) {
        //MulExp     → UnaryExp {('*' | '/' | '%') UnaryExp}
        handleUnaryExp(mulExpNode.getUnaryExpNodes().get(0));
        for (int i=0;i<mulExpNode.getOps().size();i++){
            handleUnaryExp(mulExpNode.getUnaryExpNodes().get(i+1));
            if (mulExpNode.getOps().get(i).getType() ==TokenType.MULT){
                buildFactory.createInst(OpCode.mul,index,new Value(),new Value(),currentBasicBlock);
                irCode.append("%"+index+" = MULT i32\n");
            }
            else if(mulExpNode.getOps().get(i).getType() ==TokenType.DIV){
                buildFactory.createInst(OpCode.sdiv,index,new Value(),new Value(),currentBasicBlock);
                irCode.append("%"+index+" = DIV i32\n");
            }
            else{
                buildFactory.createInst(OpCode.mod,index,new Value(),new Value(),currentBasicBlock);
                irCode.append("%"+index+" = MOD i32\n");
            }
            index++;
        }
    }

    private void handleUnaryExp(UnaryExpNode unaryExpNode) {
        //UnaryExp   → PrimaryExp | UnaryOp UnaryExp
        if (unaryExpNode.getPrimaryExpNode()!=null){
            handlePrimaryExp(unaryExpNode.getPrimaryExpNode());
        }
        else{
            if (unaryExpNode.getUnaryOpNode().getPlus()!=null){
                //irCode.append("%"+index+" =  i32 0");
            }
            else{
                buildFactory.createInst(OpCode.sub,index,new Value(),new Value(),currentBasicBlock);
                index++;
            }

            handleUnaryExp(unaryExpNode.getUnaryExpNode());
        }
    }

    private void handlePrimaryExp(PrimaryExpNode primaryExpNode) {
    }
}
