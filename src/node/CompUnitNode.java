package node;

import java.util.List;

public class CompUnitNode {
    private List<DeclNode>declNodes;
    private List<FuncDefNode>funcDefNodes;
    private MainFuncDefNode mainFuncDefNode;

    public List<FuncDefNode> getFuncDefNodes() {
        return funcDefNodes;
    }

    public MainFuncDefNode getMainFuncDefNode() {
        return mainFuncDefNode;
    }

    public CompUnitNode(List<DeclNode>declNodes, List<FuncDefNode>funcDefNodes, MainFuncDefNode mainFuncDefNode){
        this.declNodes=declNodes;
        this.funcDefNodes=funcDefNodes;
        this.mainFuncDefNode=mainFuncDefNode;
    }


    public List<DeclNode> getDeclNodes() {
        return declNodes;
    }

    public void print() {
        System.setOut(RedirectSystemOut.ps);
        for (DeclNode declNode : declNodes) {
            declNode.print();
        }
        for (FuncDefNode funcDefNode : funcDefNodes) {
            funcDefNode.print();
        }
        mainFuncDefNode.print();
        System.out.println("<CompUnit>");
    }
}
