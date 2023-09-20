package node;
import Parse
import java.util.List;

public class CompUnitNode {
    private List<DeclNode>declNodes;
    private List<FuncDefNode>funcDefNodes;
    private MainFuncDefNode mainFuncDefNode;
    public CompUnitNode(List<DeclNode>declNodes,List<FuncDefNode>funcDefNodes,MainFuncDefNode mainFuncDefNode){
        this.declNodes=declNodes;
        this.funcDefNodes=funcDefNodes;
        this.mainFuncDefNode=mainFuncDefNode;
    }
    public void print() {
        for (DeclNode declNode : declNodes) {
            declNode.print();
        }
        for (FuncDefNode funcDefNode : funcDefNodes) {
            funcDefNode.print();
        }
        mainFuncDefNode.print();
        System.out.println("CompUnit");
    }
}
