package backend;

import java.util.List;

//此类用来定义mips的指令
public class MipsInstruction {
    protected MipsType mipsType;
    protected String name=null;
    protected Integer num=null;
    protected Integer memsize=null;
    protected List<String> initParams=null;
    public MipsInstruction(MipsType type){
        this.mipsType=type;
    }
    public MipsInstruction(MipsType type,String name,Integer num){
        this.name=name;
        this.mipsType=type;
        if (type==MipsType.word) this.num=num;
        else this.memsize=num;
    }
    public MipsInstruction(MipsType type, String name, List<String> initparams){
        this.name=name;
        this.mipsType=type;
        this.initParams=initparams;
    }
    public void print(){
        if(mipsType==MipsType.data){
            System.out.println(".data");
        }
        else if (mipsType==MipsType.text){
            System.out.println(".text");
        }
        else if (mipsType==MipsType.word){
            System.out.println(name+": .word "+num);
        }
        else if (mipsType==MipsType.space){
            System.out.println(name+": .word"+memsize);
        }
    }
}
