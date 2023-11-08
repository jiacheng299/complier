package backend.primitive;

import backend.*;
import backend.Record;
import backend.optimization.Counter;
import ir.Basic.*;
import ir.Instruction.*;
import ir.Module;
import ir.Type.ValueType;
import ir.Value;

import java.util.*;
import config.*;
public class codeToMips {
    protected Module myModule;
    public Integer maxParamSize;
    public Integer tempRegisterIndex=1;
    public Function curFunction=null;
    protected MemManage myMemManage=new MemManage();
    public codeToMips(Module myModule){
        this.myModule=myModule;
        maxParamSize=findMaxParamSize();
    }
    private Integer findMaxParamSize() {
        int maxSize=0;
        //这里必须这么做，否则在call指令处无法确定函数可能的参数，内存分配可能出现问题
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
            curFunction=function;
            if (function.isDefined) handleFunc1(function);
            else handleFunc2(function);
        }
    }
    //处理declare函数
    private void handleFunc1(Function function){
        mipsInstructions.add(new MipsInstruction(MipsType.func, function.getName()));
        //如果开启了优化
        if (config.optimization){
            Counter counter=new Counter(curFunction);
            ArrayList<String> varList=counter.analyse();
            myMemManage.setGlobalReg(varList);
        }
        //先计算函数所需内存
        int memsize=calculateMemorySize(function);
        //把函数参数所需要的栈建立起来
        for (int i=0;i<maxParamSize;i++){
            Integer temp=i+4;
            myMemManage.getStackReg("param"+temp);
        }
        //寄存器保存现场需要8个栈
        for(int i=myMemManage.TEMP_DOWN;i<myMemManage.GLOBAL_UP;i++){
            myMemManage.getStackReg(RealRegister.RegName[i]);
        }
        //保存栈顶和$ra
        saveStackTop(memsize);
        //处理函数参数
        loadParams(function.getParameters(),memsize);
        //处理函数中的语句块
        boolean isFirst=true;
        for (BasicBlock block:function.getBasicBlocks()){
            if (!isFirst){
                mipsInstructions.add(new MipsInstruction(MipsType.func, curFunction.getName()+"_label_"+block.getName().replace("%","_")));
            }
            handleBlock(block);
            isFirst=false;
        }
        myMemManage.clear();
    }



    private void loadParams(List<MyParameter> parameters,Integer memsize) {
        int size=parameters.size();
        for (int i=0;i<size&&i<4;i++){
            myMemManage.tempVirtualRegList[4+i]=new VirtualRegister("%"+i);
        }
        for (int i=4;i<size;i++){
            myMemManage.virtual2Stack.put(new VirtualRegister("%"+i),new MyStack(memsize/4+i-4));
        }
    }

    private void handleBlock(BasicBlock block) {
        for (BaseInstruction instruction:block.getInstructions()){
            handleInstruction(instruction);
        }
    }



    private void handleGetElementPtr(BaseInstruction instruction) {
        GetElementPtr getElementPtr=(GetElementPtr) instruction;
        //处理类似%2 = getelementptr [3 x i32], [3 x i32]* %1, i32 0, i32 1的语句
        if (getElementPtr.bound2!=null){
            RealRegister offset=lookup(getElementPtr.bound2);
            mipsInstructions.add(new MipsInstruction(MipsType.mul, offset.name,offset.name,"4"));
            MyStack stack=myMemManage.lookupStack(instruction.value1.getName());
            RealRegister array=myMemManage.getTempReg("temp"+tempRegisterIndex++);
            //取到数组的起始地址
            if(stack!=null){
                mipsInstructions.add(new MipsInstruction(MipsType.addu, array.name,"$sp",stack.getIndex()));
            }//如果栈里没有，说明在全局变量里
            else{
                mipsInstructions.add(new MipsInstruction(MipsType.la, array.name,instruction.value1.getName().replace("@","")));
            }
            mipsInstructions.add(new MipsInstruction(MipsType.add,offset.name,array.name, offset.name));
            MyStack cunStack=myMemManage.getStackReg(getElementPtr.result.getName());
            cunStack.isArray=true;
            mipsInstructions.add(new MipsInstruction(MipsType.sw,offset.name,"$sp",cunStack.getIndex()));
            myMemManage.freeTempReg(offset);
            myMemManage.freeTempReg(array);
        }//处理类似%3 = getelementptr i32, i32* %2, i32 1的语句
        else{
            RealRegister offset=lookup(getElementPtr.bound1);
            MyStack stack=myMemManage.lookupStack(getElementPtr.value1.getName());
            RealRegister temp;
            if (stack!=null){
                temp=myMemManage.getTempReg("temp"+tempRegisterIndex++);
                mipsInstructions.add(new MipsInstruction(MipsType.lw, temp.name,"$sp", stack.getIndex()));
            }
            else{
                temp=myMemManage.lookupTemp(getElementPtr.value1.getName());
            }
            mipsInstructions.add(new MipsInstruction(MipsType.mul, offset.name,offset.name,"4"));
            mipsInstructions.add(new MipsInstruction(MipsType.add, temp.name,temp.name,offset.name));
            MyStack result=myMemManage.getStackReg(getElementPtr.result.getName());
            result.isArray=true;
            mipsInstructions.add(new MipsInstruction(MipsType.sw,temp.name,"$sp",result.getIndex()));
            myMemManage.freeTempReg(offset);
            myMemManage.freeTempReg(temp);
        }
    }

    private void handleBranchInstruction(BaseInstruction instruction) {
        BranchInstruction branchInstruction=(BranchInstruction) instruction;
        if (branchInstruction.cond!=null){
            RealRegister reg0=myMemManage.lookupTemp(branchInstruction.cond.getName());
            mipsInstructions.add(new MipsInstruction(MipsType.bne,reg0.name,"$zero",curFunction.getName()+"_label_"+branchInstruction.value1.getName().replace("%","_")));
            mipsInstructions.add(new MipsInstruction(MipsType.j,curFunction.getName()+"_label_"+branchInstruction.value2.getName().replace("%","_")));
            myMemManage.freeTempReg(reg0);
        }
        else{
            mipsInstructions.add(new MipsInstruction(MipsType.j,curFunction.getName()+"_label_"+branchInstruction.value1.getName().replace("%","_")));
        }
    }

    private void handleIcmpInstruction(BaseInstruction instruction) {
        RealRegister reg1=lookup(instruction.value1);
        RealRegister reg2=lookup(instruction.value2);
        IcmpInstruction icmpInstruction=(IcmpInstruction) instruction;
        RealRegister temp=myMemManage.getTempReg(instruction.result.getName());
        if (icmpInstruction.op==OpCode.ne){
            mipsInstructions.add(new MipsInstruction(MipsType.sne, temp.name,reg1.name,reg2.name));
        }else if (icmpInstruction.op==OpCode.eq){
            mipsInstructions.add(new MipsInstruction(MipsType.seq, temp.name,reg1.name,reg2.name));
        }else if (icmpInstruction.op==OpCode.sge){
            mipsInstructions.add(new MipsInstruction(MipsType.sge, temp.name,reg1.name,reg2.name));
        }else if (icmpInstruction.op==OpCode.sgt){
            mipsInstructions.add(new MipsInstruction(MipsType.sgt, temp.name,reg1.name,reg2.name));
        }else if (icmpInstruction.op==OpCode.sle){
            mipsInstructions.add(new MipsInstruction(MipsType.sle, temp.name,reg1.name,reg2.name));
        }else if (icmpInstruction.op==OpCode.slt){
            mipsInstructions.add(new MipsInstruction(MipsType.slt, temp.name,reg1.name,reg2.name));
        }
        myMemManage.freeTempReg(reg1);
        myMemManage.freeTempReg(reg2);
    }

    private void handleZextInstruction(BaseInstruction instruction) {
        RealRegister temp=myMemManage.lookupTemp(instruction.value1.getName());
        myMemManage.tempVirtualRegList[temp.getNum()]=new VirtualRegister(instruction.result.getName());

    }

    private void handleCallInstruction(BaseInstruction instruction) {
        CallInstruction callInstruction=(CallInstruction) instruction;
        Function function=callInstruction.function;
        //如果调用的函数不用传参
        handleParams(callInstruction.funcRParams);
        //参数之前load过，之后如果不使用的话不会free，得在这里free了
        for (int i=0;i<callInstruction.funcRParams.size();i++){
            RealRegister res=myMemManage.lookupTemp(callInstruction.funcRParams.get(i).getName());
            if (res!=null) myMemManage.freeTempReg(res);
        }
        //保存现场
        for (int i=myMemManage.TEMP_DOWN;i<myMemManage.GLOBAL_UP;i++){
            if (myMemManage.tempVirtualRegList[i].free==false){
                MyStack stack=myMemManage.lookupStack(RealRegister.RegName[i]);
                RealRegister tempRegister=myMemManage.tempRegList.get(i);
                mipsInstructions.add(new MipsInstruction(MipsType.sw, tempRegister.name,"$sp",stack.getIndex()));
                myMemManage.records.add(new backend.Record(i,stack,myMemManage.tempVirtualRegList[i].name));
                myMemManage.freeTempReg(tempRegister);
            }
        }
        //跳转到函数
        mipsInstructions.add(new MipsInstruction(MipsType.jal,function.getName()));
        //恢复现场
        for (Record record:myMemManage.records){
            mipsInstructions.add(new MipsInstruction(MipsType.lw,myMemManage.tempRegList.get(record.first).name,"$sp",record.second.getIndex()));
            myMemManage.tempVirtualRegList[record.first]=new VirtualRegister(record.third);
            myMemManage.tempVirtualRegList[record.first].free=false;
        }
        myMemManage.records.clear();
        //如果有返回值，把这个返回值存到栈里
        if (callInstruction.result!=null){
            RealRegister tempReg = myMemManage.getTempReg(callInstruction.result.getName());
            mipsInstructions.add(new MipsInstruction(MipsType.addu, tempReg.name, "$zero", "$v0"));
            //把东西存到栈里
            MyStack stack = myMemManage.getStackReg(callInstruction.result.getName());
            mipsInstructions.add(new MipsInstruction(MipsType.sw, tempReg.name, "$sp", stack.getIndex()));
            //释放寄存器
            myMemManage.freeTempReg(tempReg);
        }
    }

    private void handleParams(ArrayList<Value> funcRParams) {
        for (int i=0;i<funcRParams.size();i++){
            Value param=funcRParams.get(i);
            //如果i<4，那就存在a0-a3中
            if (i<4){
                if (!param.getName().contains("%")){
                    mipsInstructions.add(new MipsInstruction(MipsType.li,"$a"+i,param.getName()));
                }
                else{
                    RealRegister realRegister=lookup(param);
                    mipsInstructions.add(new MipsInstruction(MipsType.move,"$a"+i,realRegister.name));
                    myMemManage.freeTempReg(realRegister);
                }
            }
            else{
                if (param instanceof Const){
                    MyStack stack=myMemManage.lookupStack("param"+i);
                    RealRegister realRegister=myMemManage.getTempReg("temp"+tempRegisterIndex++);
                    mipsInstructions.add(new MipsInstruction(MipsType.li, realRegister.name,param.getName()));
                    mipsInstructions.add(new MipsInstruction(MipsType.sw, realRegister.name,"$sp", stack.getIndex()));
                    myMemManage.freeTempReg(realRegister);
                }
                else{
                    RealRegister stackRegister=lookup(param);
                    MyStack stack=myMemManage.lookupStack("param"+i);
                    mipsInstructions.add(new MipsInstruction(MipsType.sw,stackRegister.name,"$sp",stack.getIndex()));
                    myMemManage.freeTempReg(stackRegister);
                }
            }
        }
    }

    private void handleLoadInstruction(BaseInstruction instruction) {
        RealRegister resultReg = myMemManage.getTempReg(instruction.result.getName());
        MyStack stackReg = myMemManage.lookupStack(instruction.value1.getName());
        if (stackReg != null){
            if (stackReg.isArray){
                if (instruction.result.getType()==ValueType.i32){
                    RealRegister tempRegister=myMemManage.getTempReg("temp"+tempRegisterIndex++);
                    mipsInstructions.add(new MipsInstruction(MipsType.lw, tempRegister.name,"$sp",stackReg.getIndex()));
                    mipsInstructions.add(new MipsInstruction(MipsType.lw, resultReg.name,tempRegister.name,"0"));
                    myMemManage.freeTempReg(tempRegister);
                }
                else{
                    mipsInstructions.add(new MipsInstruction(MipsType.lw,resultReg.name,"$sp", stackReg.getIndex()));
                }
            }
            else mipsInstructions.add(new MipsInstruction(MipsType.lw,resultReg.name,"$sp", stackReg.getIndex()));
        }
        //找不到就去全局变量找
        else{
            mipsInstructions.add(new MipsInstruction(MipsType.lw, resultReg.name,instruction.value1.getName().replace("@","")));
        }
    }

    private void handleStoreInstruction(BaseInstruction instruction) {
        RealRegister tempReg=lookup(instruction.value1);
        MyStack stack=myMemManage.lookupStack(instruction.value2.getName());
        if (stack!=null){
            //如果这个地方存的是数组的地址
            if (stack.isArray){
                RealRegister temp=myMemManage.getTempReg("temp"+tempRegisterIndex++);
                mipsInstructions.add(new MipsInstruction(MipsType.lw,temp.name,"$sp",stack.getIndex()));
                mipsInstructions.add(new MipsInstruction(MipsType.sw, tempReg.name,temp.name,"0"));
                myMemManage.freeTempReg(temp);
            }
            else mipsInstructions.add(new MipsInstruction(MipsType.sw,tempReg.name,"$sp",stack.getIndex()));
        }
        //可能value2是全局变量
        else{
            mipsInstructions.add(new MipsInstruction(MipsType.sw, tempReg.name,instruction.value2.getName().replace("@","")));
        }
        myMemManage.freeTempReg(tempReg);
    }

    private void handleRetInstruction(BaseInstruction instruction) {
        if (curFunction.getName()=="main"){
            mipsInstructions.add(new MipsInstruction(MipsType.addiu, "$v0", "$zero", "10"));
            mipsInstructions.add(new MipsInstruction(MipsType.syscall, ""));
        }
        else{
            ValueType type=((RetInstruction)instruction).type;
            //有返回值
            if (type!=ValueType.VOID){
                RealRegister tempRegister=lookup(instruction.result);
                mipsInstructions.add(new MipsInstruction(MipsType.addu,"$v0","$zero",tempRegister.name));
                myMemManage.freeTempReg(tempRegister);
            }
            fixStackTop();
            mipsInstructions.add(new MipsInstruction(MipsType.jr,"$ra"));
        }
    }

    private void fixStackTop() {
        MyStack stack=myMemManage.lookupStack("$ra");
        mipsInstructions.add(new MipsInstruction(MipsType.lw,"$ra","$sp",stack.getIndex()));
        stack=myMemManage.lookupStack("$sp");
        mipsInstructions.add(new MipsInstruction(MipsType.lw,"$sp","$sp",stack.getIndex()));
    }

    private void handleBinaryInstruction(BaseInstruction instruction) {
        RealRegister reg1=lookup(instruction.value1);
        RealRegister reg2=lookup(instruction.value2);
        RealRegister reg0=myMemManage.getTempReg(instruction.result.getName());
        if (((BinaryInstruction) instruction).opCode==OpCode.add){
            mipsInstructions.add(new MipsInstruction(MipsType.addu,reg0.name,reg1.name,reg2.name));
        }
        else if (((BinaryInstruction) instruction).opCode==OpCode.sub){
            mipsInstructions.add(new MipsInstruction(MipsType.sub,reg0.name,reg1.name,reg2.name));
        }
        else if (((BinaryInstruction) instruction).opCode==OpCode.mul){
            mipsInstructions.add(new MipsInstruction(MipsType.mul,reg0.name,reg1.name,reg2.name));
        }
        else if (((BinaryInstruction) instruction).opCode==OpCode.sdiv){
            mipsInstructions.add(new MipsInstruction(MipsType.div,reg1.name,reg2.name));
            mipsInstructions.add(new MipsInstruction(MipsType.mflo,reg0.name));
        }
        else if (((BinaryInstruction) instruction).opCode==OpCode.srem){
            mipsInstructions.add(new MipsInstruction(MipsType.div,reg1.name,reg2.name));
            mipsInstructions.add(new MipsInstruction(MipsType.mfhi,reg0.name));
        }
        MyStack stack=myMemManage.getStackReg(instruction.result.getName());
        mipsInstructions.add(new MipsInstruction(MipsType.sw, reg0.name,"$sp",stack.getIndex()));
        myMemManage.freeTempReg(reg0);
        myMemManage.freeTempReg(reg1);
        myMemManage.freeTempReg(reg2);
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
        //先存储ra，sp和t0-t7 10个寄存器
        int memsize=4*(2+ myMemManage.GLOBAL_UP-myMemManage.TEMP_DOWN);
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
                instruction=new MipsInstruction(MipsType.space,global.getName().replace("@",""),Integer.parseInt(global.onearrayNum)*4);
            }
            else{
                List<String> tempList=global.arrayNum;
                //Collections.reverse(tempList);
                instruction = new MipsInstruction(MipsType.word,global.getName().replace("@",""), tempList);
            }
        }//如果是二维数组
        else{
            if (global.arrayNum == null){
                instruction = new MipsInstruction(MipsType.space, global.getName().replace("@",""), Integer.parseInt(global.onearrayNum)*Integer.parseInt(global.twoarrayNum)*4);
            }
            else{
                List<String> tempList=global.arrayNum;
                instruction = new MipsInstruction(MipsType.word,global.getName().replace("@",""), tempList);
            }
        }
        mipsInstructions.add(instruction);
        myMemManage.globalSet.add(global.getName().replace("@",""));
    }
    //找到除了全局变量以外的所有变量所处寄存器位置
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
                //看看是否存在全局寄存器
                temp=myMemManage.lookupGlobalReg(value.getName());
                if (temp!=null) return temp;
                //看看栈中有没有这个value
                MyStack stack=myMemManage.lookupStack(value.getName());
                //拿到t0-t7中的一个空闲寄存器
                temp =myMemManage.getTempReg(value.getName());
                mipsInstructions.add(new MipsInstruction(MipsType.lw, temp.name,"$sp",stack.getIndex()));
            }

        }
        return temp;
    }
    private void handleInstruction(BaseInstruction instruction) {
        mipsInstructions.add(new MipsInstruction(MipsType.debug,instruction));
        if (config.optimization){
            if (instruction instanceof AllocateInstruction) handleAllocateInstruction(instruction,true);
            else if (instruction instanceof BinaryInstruction) handleBinaryInstruction(instruction,true);
            else if (instruction instanceof BranchInstruction) handleBranchInstruction(instruction,true);
            else if (instruction instanceof CallInstruction) handleCallInstruction(instruction,true);
            else if (instruction instanceof GetElementPtr) handleGetElementPtr(instruction,true);
            else if (instruction instanceof IcmpInstruction) handleIcmpInstruction(instruction,true);
            else if (instruction instanceof LoadInstruction) handleLoadInstruction(instruction,true);
            else if (instruction instanceof RetInstruction) handleRetInstruction(instruction,true);
            else if (instruction instanceof StoreInstruction) handleStoreInstruction(instruction,true);
            else if (instruction instanceof ZextInstruction) handleZextInstruction(instruction,true);
        }
        else{
            if (instruction instanceof AllocateInstruction) handleAllocateInstruction(instruction);
            else if (instruction instanceof BinaryInstruction) handleBinaryInstruction(instruction);
            else if (instruction instanceof BranchInstruction) handleBranchInstruction(instruction);
            else if (instruction instanceof CallInstruction) handleCallInstruction(instruction);
            else if (instruction instanceof GetElementPtr) handleGetElementPtr(instruction);
            else if (instruction instanceof IcmpInstruction) handleIcmpInstruction(instruction);
            else if (instruction instanceof LoadInstruction) handleLoadInstruction(instruction);
            else if (instruction instanceof RetInstruction) handleRetInstruction(instruction);
            else if (instruction instanceof StoreInstruction) handleStoreInstruction(instruction);
            else if (instruction instanceof ZextInstruction) handleZextInstruction(instruction);
        }

    }

    private void handleBinaryInstruction(BaseInstruction instruction, boolean b) {
        handleBinaryInstruction(instruction);
    }

    private void handleGetElementPtr(BaseInstruction instruction, boolean b) {
        GetElementPtr getElementPtr=(GetElementPtr) instruction;
        //处理类似%2 = getelementptr [3 x i32], [3 x i32]* %1, i32 0, i32 1的语句
        if (getElementPtr.bound2!=null){
            RealRegister offset=lookup(getElementPtr.bound2);
            RealRegister tempReg=myMemManage.getTempReg("temp"+tempRegisterIndex++);
            mipsInstructions.add(new MipsInstruction(MipsType.mul, tempReg.name,offset.name,"4"));
            MyStack stack=myMemManage.lookupStack(instruction.value1.getName());
            RealRegister array=myMemManage.getTempReg("temp"+tempRegisterIndex++);
            //取到数组的起始地址
            if(stack!=null){
                mipsInstructions.add(new MipsInstruction(MipsType.addu, array.name,"$sp",stack.getIndex()));
            }//如果栈里没有，说明在全局变量里
            else{
                mipsInstructions.add(new MipsInstruction(MipsType.la, array.name,instruction.value1.getName().replace("@","")));
            }
            mipsInstructions.add(new MipsInstruction(MipsType.add,tempReg.name,array.name, tempReg.name));
            MyStack cunStack=myMemManage.getStackReg(getElementPtr.result.getName());
            cunStack.isArray=true;
            mipsInstructions.add(new MipsInstruction(MipsType.sw,tempReg.name,"$sp",cunStack.getIndex()));
            myMemManage.freeTempReg(tempReg);
            myMemManage.freeTempReg(offset);
            myMemManage.freeTempReg(array);
        }//处理类似%3 = getelementptr i32, i32* %2, i32 1的语句
        else{
            RealRegister offset=lookup(getElementPtr.bound1);
            RealRegister tempReg=myMemManage.getTempReg("temp"+tempRegisterIndex++);
            RealRegister tempReg2=myMemManage.lookupGlobalReg(getElementPtr.value1.getName());
            RealRegister temp;
            //这个value1的值不一定在栈里，也可能存在全局寄存器了
            if (tempReg2!=null){
                temp=myMemManage.getTempReg("temp"+tempRegisterIndex++);
            }
            else{
                MyStack stack=myMemManage.lookupStack(getElementPtr.value1.getName());
                if (stack!=null){
                    temp=myMemManage.getTempReg("temp"+tempRegisterIndex++);
                    mipsInstructions.add(new MipsInstruction(MipsType.lw, temp.name,"$sp", stack.getIndex()));
                }
                else{
                    temp=myMemManage.lookupTemp(getElementPtr.value1.getName());
                }
            }

            mipsInstructions.add(new MipsInstruction(MipsType.mul, tempReg.name,offset.name,"4"));
            mipsInstructions.add(new MipsInstruction(MipsType.add, temp.name,tempReg2.name,tempReg.name));
            MyStack result=myMemManage.getStackReg(getElementPtr.result.getName());
            result.isArray=true;
            mipsInstructions.add(new MipsInstruction(MipsType.sw,temp.name,"$sp",result.getIndex()));
            myMemManage.freeTempReg(tempReg);
            myMemManage.freeTempReg(offset);
            myMemManage.freeTempReg(temp);
        }
    }

    private void handleCallInstruction(BaseInstruction instruction, boolean b) {
        //目前先不做优化
        handleCallInstruction(instruction);
    }

    private void handleIcmpInstruction(BaseInstruction instruction, boolean b) {
        handleIcmpInstruction(instruction);
    }

    private void handleRetInstruction(BaseInstruction instruction, boolean b) {
        handleRetInstruction(instruction);
    }

    private void handleBranchInstruction(BaseInstruction instruction, boolean b) {
        handleBranchInstruction(instruction);
    }

    private void handleStoreInstruction(BaseInstruction instruction, boolean b) {
        RealRegister globalReg=myMemManage.lookupTemp(instruction.value2.getName());
        if (globalReg!=null){
            RealRegister tempReg=lookup(instruction.value1);
            mipsInstructions.add(new MipsInstruction(MipsType.move,globalReg.name,tempReg.name));
            myMemManage.freeTempReg(tempReg);
            return;
        }
        handleStoreInstruction(instruction);
    }

    private void handleZextInstruction(BaseInstruction instruction, boolean b) {
        handleZextInstruction(instruction);
    }

    private void handleLoadInstruction(BaseInstruction instruction, boolean b) {
        RealRegister globalReg=myMemManage.lookupTemp(instruction.value1.getName());
        if (globalReg!=null){
            myMemManage.virtual2Reg.put(instruction.result.getName(),globalReg);
            return;
        }
        handleLoadInstruction(instruction);
    }

    private void handleAllocateInstruction(BaseInstruction instruction, boolean b) {
        String varName=instruction.value1.getName();
        //相比优化前来说，有可能这个值被存到全局寄存器了
        if (myMemManage.lookupTemp(varName)!=null){
            return;
        }
        else{
            handleAllocateInstruction(instruction);
        }
    }

}
