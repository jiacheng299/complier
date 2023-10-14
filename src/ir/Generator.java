package ir;

import Token.TokenType;
import ir.Basic.BasicBlock;
import ir.Basic.Function;
import ir.Instruction.OpCode;
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

}
