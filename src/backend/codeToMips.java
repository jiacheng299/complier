package backend;

import ir.Basic.*;
import ir.Instruction.*;
import ir.Module;
import ir.Type.ValueType;
import ir.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class codeToMips {
    protected Module myModule;
    protected MemManage myMemManage=new MemManage();

    private Integer findMaxParamSize() {
        int maxSize=0;
        for (Function function:myModule.getFunctions()){
            maxSize=Math.max(maxSize,function.getParameters().size());
        }
        if (maxSize>4) return maxSize-4;
        else return 0;
    }

    protected List<MipsInstruction> mipsInstructions=new ArrayList<MipsInstruction>();
    public void start(){
        //首先处理data段
        handleData();
        //然后处理text段
        //最后处理
    }
    //
    private void handleData() {
        mipsInstructions.add(new MipsInstruction(MipsType.data));
        for (GlobalVar global:myModule.getGlobalVars()) {
            handleGlobal(global);
        }
        mipsInstructions.add(new MipsInstruction(MipsType.text));
        //mipsInstructions.add(new MipsInstruction(MipsType.global));
        Collections.reverse(myModule.getFunctions());
        List<Function> functions=myModule.getFunctions();
        for (Function function:functions){

        }
    }
    //处理declare函数
    private void handleFunc1(Function function){
        mipsInstructions.add(new MipsInstruction(MipsType.func, function.getName()));
        //先计算函数所需内存
        int memsize=calculateMemorySize(function);
    }

    private int calculateMemorySize(Function function) {
        //先存储ra，sp和s0-s7 10个寄存器
        int memsize=4*10;
        //函数调用参数可能还要加
        memsize+=findMaxParamSize()*4;
        for (BasicBlock block : function.getBasicBlocks()){
            for (BaseInstruction instruction:block.getInstructions()){
                if (instruction instanceof AllocateInstruction){
                    if(instruction.value1.getType()==ValueType.i32) memsize+=4;
                    else if (instruction.value1.getType()==ValueType.onearray) memsize+=Integer.parseInt(instruction.value1.onearrayNum)*4;
                    else if (instruction.value1.getType()==ValueType.twoarray) memsize+=Integer.parseInt(instruction.value1.onearrayNum)*Integer.parseInt(instruction.value1.twoarrayNum) * 4;
                }//二元表达式 a+b 需要把这个值临时存起来
                else if (instruction instanceof BinaryInstruction){
                    memsize += 4;
                }//如果是数组取地址指令
                else if (instruction instanceof GetElementPtr){
                    memsize += 4;
                }//call指令有俩种情况，有返回值和无返回值
                else if (instruction instanceof CallInstruction){
                    if (instruction.result!=null){
                        memsize +=4;
                    }
                }
            }
        }
        return memsize;
    }

    //处理define函数
    private void handleFunc2(Function function){
        mipsInstructions.add(new MipsInstruction(MipsType.func, function.getName()));
        if (function.getName().equals("putch")) mipsInstructions.add(new MipsInstruction(MipsType.li,"$v0","11"));
        else if (function.getName().equals("putint")) mipsInstructions.add(new MipsInstruction(MipsType.li,"$v0","1"));
        else if (function.getName().equals("getint")) mipsInstructions.add(new MipsInstruction(MipsType.li,"$v0","5"));
        mipsInstructions.add(new MipsInstruction(MipsType.syscall));
        mipsInstructions.add(new MipsInstruction(MipsType.jr,"$ra"));
    }
//处理data段，主要就是处理全局变量和函数
    private void handleGlobal(GlobalVar global) {
        MipsInstruction instruction=null;
        //如果是int
        if (global.getType()== ValueType.i32) {
            instruction = new MipsInstruction(MipsType.word,global.getName().replace("@",""),global.getNum());
        }//如果是一维数组
        else if (global.getType() == ValueType.onearray){
            if(global.arrayNum==null){
                instruction=new MipsInstruction(MipsType.space,global.getName().replace("@",""),Integer.parseInt(global.onearrayNum));
            }
            else{
                List<String> tempList=global.arrayNum;
                Collections.reverse(tempList);
                instruction = new MipsInstruction(MipsType.word,global.getName().replace("@",""), tempList);
            }
        }//如果是二维数组
        else{
            if (global.arrayNum == null){
                instruction = new MipsInstruction(MipsType.space, global.getName().replace("@",""), Integer.parseInt(global.onearrayNum)*Integer.parseInt(global.twoarrayNum));
            }
            else{
                List<String> tempList=global.arrayNum;
                Collections.reverse(tempList);
                instruction = new MipsInstruction(MipsType.word,global.getName().replace("@",""), tempList);
            }
        }
        mipsInstructions.add(instruction);
        myMemManage.globalSet.add(global.getName().replace("@",""));
    }

}
