package backend;
//这个类用来进行内存管理

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class MemManage {
    public HashSet<String> globalSet = new HashSet<>();
    public Integer stackPointer=0;
    public HashMap<VirtualRegister, MyStack> virtual2Stack=new HashMap<>();
    public List<RealRegister>tempRegList=new ArrayList<>();
    public HashSet<String> params=new HashSet<>();
    public VirtualRegister[] tempVirtualRegList=new VirtualRegister[RealRegister.RegName.length];
    public MemManage(){
        init();
    }

    private void init() {
        for (int i=0;i<RealRegister.RegName.length;i++){
            tempRegList.add(new RealRegister(RealRegister.RegName[i]));
        }
    }

    public MyStack getStackReg(String virtualNum) {
        MyStack res = new MyStack(stackPointer++);
        virtual2Stack.put(new VirtualRegister(virtualNum), res);
        return res;
    }
    public MyStack getStackReg(String virtualNum,Integer num) {
        MyStack res = new MyStack(stackPointer);
        virtual2Stack.put(new VirtualRegister(virtualNum), res);
        stackPointer+=num;
        return res;
    }
    public RealRegister getTempReg(String virtualNum) {
        for (int i=8;i<17;i++){
            if (tempVirtualRegList[i].free=true){
                //看看哪个虚拟寄存器没用，没用的就用上
                tempVirtualRegList[i]=new VirtualRegister(virtualNum);
                tempVirtualRegList[i].free=false;
                return tempRegList.get(i);
            }
        }
        return null;
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
        tempVirtualRegList[tempReg.getNum()].free=true;
    }
    public boolean isParameter(String str){
        if (params.contains(str)) return true;
        else return false;
    }
}
