package ir;

import node.*;
import symbol.SymbolInfo;
import symbol.SymbolTableNode;

public class Generator {
    private SymbolTableNode currentNode=SymbolTableNode.getCurrentNode();
    private SymbolInfo currentFunction=null;
    private SymbolInfo returnNum=null;
    private Integer index=1;
    private static Generator generator=new Generator();
    public void Generator(){}
    public void start(CompUnitNode compUnitNode){

        handleMainFunc(compUnitNode.getMainFuncDefNode());
    }

    private void handleMainFunc(MainFuncDefNode mainFuncDefNode) {
        SymbolInfo symbolInfo=new SymbolInfo("main", SymbolInfo.SymbolType.func,currentNode, SymbolInfo.ReturnType.INT);
        currentNode.addSymbol("main",symbolInfo);
        if(symbolInfo.getType()==SymbolInfo.SymbolType.func){
            if (symbolInfo.getReturnType() == SymbolInfo.ReturnType.INT){
                System.out.println("define dso_local i32 @"+symbolInfo.getName()+"()"+" {");
            }
        }
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
        if (stmtnode.getReturntk() != null){
            handleExp(stmtnode.getExpNode());
            if (currentFunction.getReturnType() ==SymbolInfo.ReturnType.INT)
            System.out.println("ret i32 %"+returnNum.getId());
        }
    }

    private void handleExp(ExpNode expNode) {
    }
}
