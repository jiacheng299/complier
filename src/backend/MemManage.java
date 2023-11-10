package backend;
//这个类用来进行内存管理

import backend.MyStack;
import backend.RealRegister;
import backend.Record;
import backend.VirtualRegister;

import java.util.*;
import config.*;
public class MemManage {
    public Integer stackPointer=0;
    public HashMap<VirtualRegister, MyStack> virtual2Stack=new HashMap<>();
    public HashMap<String,RealRegister> virtual2Reg=new HashMap<>();
    public List<RealRegister>tempRegList=new ArrayList<>();
    public VirtualRegister[] tempVirtualRegList=new VirtualRegister[RealRegister.RegName.length];
    public List<Record> records=new ArrayList<>();
    public Integer TEMP_DOWN=8;
    public Integer TEMP_UP=16;
    public Integer GLOBAL_DOWN;
    public Integer GLOBAL_UP=16;
    public MemManage(){
        init();
    }

    private void init() {
        if (config.optimization){
            TEMP_UP=14;
            GLOBAL_DOWN=TEMP_UP;
            GLOBAL_UP=14;
        }
        for (int i=0;i<RealRegister.RegName.length;i++){
            tempRegList.add(new RealRegister(RealRegister.RegName[i]));
            tempVirtualRegList[i]=new VirtualRegister();
        }
        for (int i=TEMP_DOWN;i<GLOBAL_UP;i++){
            tempVirtualRegList[i].free=true;
        }
    }

    public MyStack getStackReg(String virtualNum) {
        MyStack res = new MyStack(stackPointer++);
        virtual2Stack.put(new VirtualRegister(virtualNum), res);
        return res;
    }
    public MyStack getStackReg(String virtualNum,Integer num) {
        MyStack res = new MyStack(stackPointer);
        res.isArray=true;
        virtual2Stack.put(new VirtualRegister(virtualNum), res);
        stackPointer+=num;
        return res;
    }
    public RealRegister getTempReg(String virtualNum) {
        for (int i=TEMP_DOWN;i<TEMP_UP;i++){
            if (tempVirtualRegList[i].free==true){
                //看看哪个虚拟寄存器没用，没用的就用上
                tempVirtualRegList[i]=new VirtualRegister(virtualNum);
                tempVirtualRegList[i].free=false;
                return tempRegList.get(i);
            }
        }
        MyStack temp=lookupStack(tempVirtualRegList[TEMP_DOWN].name);
        MyStack cun;
        if (temp==null) cun=getStackReg(tempVirtualRegList[TEMP_DOWN].name);
        else cun=temp;
        codeToMips.codegen.mipsInstructions.add(new MipsInstruction(MipsType.sw,tempRegList.get(TEMP_DOWN).name,"$sp", cun.getIndex()));
        tempVirtualRegList[TEMP_DOWN]=new VirtualRegister(virtualNum);
        return tempRegList.get(TEMP_DOWN);
    }
    public RealRegister lookupTemp(String virtualNum){
        RealRegister res = null;
        for (int i=0; i< tempVirtualRegList.length; i++){
            if (tempVirtualRegList[i].name.equals(virtualNum)){
                res=tempRegList.get(i);
            }
        }
        return res;
    }
    public MyStack lookupStack(String name){
        return virtual2Stack.getOrDefault(new VirtualRegister(name),null);
    }

    public void freeTempReg(RealRegister tempReg) {
        if (tempReg.getNum()>=TEMP_DOWN&&tempReg.getNum()<TEMP_UP){
            tempVirtualRegList[tempReg.getNum()].free=true;
            tempVirtualRegList[tempReg.getNum()].name="";
        }
    }

    public void clear() {
        stackPointer=0;
        virtual2Stack.clear();
        virtual2Reg.clear();
        for (int i=TEMP_DOWN;i<GLOBAL_UP;i++){
            VirtualRegister virtualRegister=tempVirtualRegList[i];
            virtualRegister.free=true;
            virtualRegister.name="";
        }
    }

    public void setGlobalReg(ArrayList<String> varList) {
        //逐个分配寄存器
        for (int i=GLOBAL_DOWN;i<GLOBAL_UP;i++){
            if (i-GLOBAL_DOWN<varList.size()){
                tempVirtualRegList[i]=new VirtualRegister(varList.get(i-GLOBAL_DOWN));
            }
        }
    }

    public RealRegister lookupGlobalReg(String name) {
        return virtual2Reg.get(name);
    }
}
