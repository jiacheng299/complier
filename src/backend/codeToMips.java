package backend;

import ir.Basic.GlobalVar;
import ir.Module;
import ir.Type.ValueType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class codeToMips {
    protected Module myModule;
    protected List<MipsInstruction> mipsInstructions=new ArrayList<MipsInstruction>();
    public void start(){
        //首先处理data段
        handleData();
        //然后处理text段
        //最后处理
    }
    //
    private void handleData() {
        MipsInstruction instruction = new MipsInstruction(MipsType.data);

        for (GlobalVar global:myModule.getGlobalVars()) {
            handleGlobal(global);
        }
    }
//处理data段，主要就是处理全局变量和函数
    private void handleGlobal(GlobalVar global) {
        //如果是int
        if (global.getType()== ValueType.i32) {
            MipsInstruction instruction = new MipsInstruction(MipsType.word,global.getName().replace("@",""),global.getNum());
        }//如果是一维数组
        else if (global.getType() == ValueType.onearray){
            if(global.arrayNum==null){
                MipsInstruction instruction=new MipsInstruction(MipsType.space,global.getName().replace("@",""),Integer.parseInt(global.onearrayNum));
            }
            else{
                List<String> tempList=global.arrayNum;
                Collections.reverse(tempList);
                MipsInstruction instruction = new MipsInstruction(MipsType.word,global.getName().replace("@",""), tempList);
            }
        }//如果是二维数组
        else{

        }
    }
}
