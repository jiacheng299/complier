package backend;


import newIR.Instruction.*;
import newIR.Module.BasicBlock;
import newIR.Module.Function;
import newIR.ValueSon.Global;
import newIR.ValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

//用来统计每个变量的权重
public class Counter {
    public Integer weight=1;
    public Integer loop_weight=30;
    public Function function;
    public HashMap<String,Integer> var2weight;
    public HashSet<String> NO;
    public Counter(Function function){
        this.function=function;
        this.var2weight=new HashMap<>();
        this.NO=new HashSet<>();
    }
    public ArrayList<String> analyse(){
        for (BasicBlock basicBlock: function.basicBlocks){
            //循环块要加权重,考虑内层可能还有循环
            if (basicBlock.enterLoop)   weight*=loop_weight;
            if (basicBlock.exitLoop)    weight/=loop_weight;
            for (Instruction instruction:basicBlock.instructions){
                handleInst(instruction);
            }
        }
        ArrayList<String> sortedRef = new ArrayList<>();
        var2weight.entrySet().stream().sorted((o1, o2) -> o2.getValue() - o1.getValue()).
                forEachOrdered(x -> sortedRef.add(x.getKey()));
        return sortedRef;
    }

    private void handleInst(Instruction instruction) {
        if (instruction instanceof AllocateInstruction){
            if (instruction.result.valueType!= ValueType.i32){
                NO.add(instruction.result.name);
            }
        }
        else if(instruction instanceof GetElementPtr){
            NO.add(instruction.result.name);
        }
        //只有对内存有操作的指令才计数
        else if (instruction instanceof LoadInstruction){
            String varName=instruction.value1.name;
            if (instruction.value1 instanceof Global) return;
            if (NO.contains(varName)) return;
            if (var2weight.containsKey(varName)){
                var2weight.put(varName,var2weight.get(varName)+weight);
            }
            else{
                var2weight.put(varName,weight);
            }
        }
        else if (instruction instanceof StoreInstruction){
            String varName=instruction.value2.name;
            if (instruction.value2 instanceof Global) return;
            if (NO.contains(varName)) return;
            if (var2weight.containsKey(varName)){
                var2weight.put(varName,var2weight.get(varName)+weight);
            }
            else{
                var2weight.put(varName,weight);
            }
        }
    }
}
