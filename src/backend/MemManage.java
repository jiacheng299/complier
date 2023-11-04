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
}
