package newIR.Module;

import newIR.Value;
import newIR.ValueType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Function {
    public String name;
    public ValueType returnType;
    public boolean isDefined=false;
    public List<Value> params;
    public List<BasicBlock> basicBlocks=new ArrayList<BasicBlock>();
    public Function(String name, ValueType returnType, boolean isDefined){
        this.name=name;
        this.returnType=returnType;
        this.isDefined=isDefined;
        this.params=new ArrayList<Value>();
    }

    public void appendBlock(BasicBlock currentBlock) {
        basicBlocks.add(currentBlock);
    }
    public void sortBasicBlocks() {
        Collections.sort(basicBlocks, new Comparator<BasicBlock>() {
            @Override
            public int compare(BasicBlock bb1, BasicBlock bb2) {
                // 根据自定义的比较规则进行比较
                return Integer.parseInt(bb1.name.replaceAll("[^0-9]", ""))-
                        Integer.parseInt(bb2.name.replaceAll("[^0-9]", ""));

            }
        });
    }
    public void print() {
        sortBasicBlocks();
        if (this.isDefined==false){
            if (returnType== ValueType.i32)
                System.out.print("declare "+returnType.toString().toLowerCase()+" @"+name+"(");
            else if(returnType== ValueType.VOID)
                System.out.print("declare "+returnType.toString().toLowerCase()+" @"+name+"(");
            for(int i=0;i<params.size();i++){
                if (params.get(i).valueType== ValueType.pointer) {
                    System.out.print("i32*");
                } else System.out.print(params.get(i).valueType);
                if (i!= params.size()-1){
                    System.out.print(",");
                }
            }
            //加入一系列参数
            System.out.println(")");
        }
        else{
            if (returnType== ValueType.i32)
                System.out.print("define dso_local "+returnType+" @"+name+"(");
            else if(returnType== ValueType.VOID)
                System.out.print("define dso_local "+returnType.toString().toLowerCase()+" @"+name+"(");
            for(int i=0;i<params.size();i++){
                if (params.get(i).valueType== ValueType.pointer) {
                    System.out.print("i32*");
                } else System.out.print(params.get(i).valueType);
                if (i!= params.size()-1){
                    System.out.print(",");
                }
            }
            //加入一系列参数
            System.out.println(")");
            System.out.println("{");
            for(BasicBlock basicBlock:basicBlocks){
                basicBlock.print();
            }
            System.out.println("}");
        }
    }
}
