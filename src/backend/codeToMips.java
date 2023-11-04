package backend;

import ir.Basic.*;
import ir.Instruction.*;
import ir.Module;
import ir.Type.ValueType;
import ir.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.ReadWriteLock;

public class codeToMips {
    protected Module myModule;
    public Integer maxParamSize;
    public Integer tempRegisterIndex=1;
    protected MemManage myMemManage=new MemManage();
    public codeToMips(Module myModule){
        this.myModule=myModule;
        maxParamSize=findMaxParamSize();
    }
    private Integer findMaxParamSize() {
        int maxSize=0;
        for (Function function:myModule.getFunctions()){
            maxSize=Math.max(maxSize,function.getParameters().size());
        }
        if (maxSize>4) return maxSize-4;
        else return 0;
    }

    public List<MipsInstruction> mipsInstructions=new ArrayList<MipsInstruction>();
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
            if (function.isDefined) handleFunc1(function);
            else handleFunc2(function);
        }
    }
    //处理declare函数
    private void handleFunc1(Function function){
        mipsInstructions.add(new MipsInstruction(MipsType.func, function.getName()));
        //先计算函数所需内存
        int memsize=calculateMemorySize(function);
        //把函数参数所需要的栈建立起来
        for (int i=0;i<maxParamSize;i++){
            Integer temp=i+4;
            myMemManage.getStackReg("param"+temp);
        }
        //寄存器保存现场需要8个栈
        for(int i=8;i<17;i++){
            myMemManage.getStackReg(RealRegister.RegName[i]);
        }
        //保存栈顶和$ra
        saveStackTop(memsize);
        //处理函数参数
        //处理函数中的语句块
        for (BasicBlock block:function.getBasicBlocks()){
            handleBlock(block);
        }
    }

    private void handleBlock(BasicBlock block) {
        for (BaseInstruction instruction:block.getInstructions()){
            handleInstruction(instruction);
        }
    }

    private void handleInstruction(BaseInstruction instruction) {
        mipsInstructions.add(new MipsInstruction(MipsType.debug,instruction));
        if (instruction instanceof AllocateInstruction) handleAllocateInstruction(instruction);
        else if (instruction instanceof BinaryInstruction) handleBinaryInstruction(instruction);
//        else if (instruction instanceof BranchInstruction) handleBranchInstruction(instruction);
//        else if (instruction instanceof CallInstruction) handleCallInstruction(instruction);
//        else if (instruction instanceof GetElementPtr) handleGetElementPtr(instruction);
//        else if (instruction instanceof IcmpInstruction) handleIcmpInstruction(instruction);
        else if (instruction instanceof LoadInstruction) handleLoadInstruction(instruction);
        else if (instruction instanceof RetInstruction) handleRetInstruction(instruction);
        else if (instruction instanceof StoreInstruction) handleStoreInstruction(instruction);
//        else if (instruction instanceof ZextInstruction) handleZextInstruction(instruction);
    }

    private void handleLoadInstruction(BaseInstruction instruction) {
        //如果要加载的参数是函数参数
        if (myMemManage.isParameter(instruction.result.getName())){
            MyStack stack = myMemManage.lookupStack(instruction.result.getName());
        }
        else{
            RealRegister resultReg = myMemManage.getTempReg(instruction.result.getName());
            MyStack stackReg = myMemManage.lookupStack(instruction.value1.getName());
            if (stackReg != null){
                mipsInstructions.add(new MipsInstruction(MipsType.lw,resultReg.name,"$sp", stackReg.getIndex()));
            }
            //找不到就去全局变量找
            else{
                mipsInstructions.add(new MipsInstruction(MipsType.lw, resultReg.name,instruction.value1.getName()));
            }
        }
    }

    private void handleStoreInstruction(BaseInstruction instruction) {
        RealRegister tempReg=lookup(instruction.value1);
        MyStack stack=myMemManage.lookupStack(instruction.value2.getName());
        if (stack!=null){
            mipsInstructions.add(new MipsInstruction(MipsType.sw,tempReg.name,"$sp",stack.getIndex()));
        }
        //可能value2是全局变量
        else{
            //mipsInstructions.add(new MipsInstruction())
        }
        myMemManage.freeTempReg(tempReg);
    }

    private void handleRetInstruction(BaseInstruction instruction) {
        mipsInstructions.add(new MipsInstruction(MipsType.addiu, "$v0", "$zero", "10"));
        mipsInstructions.add(new MipsInstruction(MipsType.syscall, ""));
    }

    private void handleBinaryInstruction(BaseInstruction instruction) {
        if (((BinaryInstruction) instruction).opCode==OpCode.add){
            RealRegister reg1=lookup(instruction.value1);
            RealRegister reg2=lookup(instruction.value2);
            RealRegister reg0=myMemManage.getTempReg(instruction.result.getName());
            mipsInstructions.add(new MipsInstruction(MipsType.addu,reg0.name,reg1.name,reg2.name));
        }
        else if (((BinaryInstruction) instruction).opCode==OpCode.sub){

        }
        else if (((BinaryInstruction) instruction).opCode==OpCode.mul){

        }
        else if (((BinaryInstruction) instruction).opCode==OpCode.sdiv){

        }
        else if (((BinaryInstruction) instruction).opCode==OpCode.srem){

        }
    }

    private void handleAllocateInstruction(BaseInstruction instruction) {
        //如果是申请内存的话，我把它放到栈里
        String virtualName=instruction.result.getName();
        ValueType valueType=instruction.value1.getType();
        if (valueType==ValueType.onearray){
            myMemManage.getStackReg(virtualName,Integer.parseInt(instruction.value1.onearrayNum));
        }
        else if (valueType==ValueType.twoarray){
            myMemManage.getStackReg(virtualName,Integer.parseInt(instruction.value1.onearrayNum)*Integer.parseInt(instruction.value1.twoarrayNum));
        }
        else{
            myMemManage.getStackReg(virtualName);
        }
    }


    private void saveStackTop(int memsize) {
        mipsInstructions.add(new MipsInstruction(MipsType.addu,"$t0","$sp","$zero"));
        MyStack stack=myMemManage.getStackReg("$sp");
        mipsInstructions.add(new MipsInstruction(MipsType.addiu,"$sp","$sp","-"+memsize));
        mipsInstructions.add(new MipsInstruction(MipsType.sw,"$t0","$sp",stack.getIndex()));
        stack = myMemManage.getStackReg("$ra");
        mipsInstructions.add(new MipsInstruction( MipsType.sw, "$ra", "$sp", stack.getIndex()));
    }

    private int calculateMemorySize(Function function) {
        //先存储ra，sp和s0-s7 10个寄存器
        int memsize=4*10;
        //函数调用参数可能还要加
        memsize+=maxParamSize*4;
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
    private RealRegister lookup(Value value){
        RealRegister temp;
        if(value instanceof Const){
            //拿到t0-t7中的一个空闲寄存器
            temp=myMemManage.getTempReg("tempReg"+tempRegisterIndex++);
            //把这个const的值存到对应的寄存器中
            mipsInstructions.add(new MipsInstruction(MipsType.addiu,temp.name,"$zero",value.getName()));
        }
        else{
            //看看value是否已经存到某个寄存器中了
            temp=myMemManage.lookupTemp(value.getName());
            if(temp==null){
                //看看栈中有没有这个value
                MyStack stack=myMemManage.lookupStack(value.getName());
                //拿到t0-t7中的一个空闲寄存器
                temp =myMemManage.getTempReg(value.getName());
                mipsInstructions.add(new MipsInstruction(MipsType.lw, temp.name,"$sp",stack.getIndex()));
            }

        }
        return temp;
    }
}
