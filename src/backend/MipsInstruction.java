package backend;

import java.util.List;

//此类用来定义mips的指令
public class MipsInstruction {
    private  String reg1;
    private String reg2;
    private String reg3;
    protected MipsType mipsType;
    protected String name=null;
    protected Integer num=null;
    protected Integer memsize=null;
    protected List<String> initParams=null;
    public MipsInstruction(MipsType type){
        this.mipsType=type;
    }
    public MipsInstruction(MipsType type,String name){
        this.mipsType=type;
        this.name=name;
    }
    public MipsInstruction(MipsType type,String reg1,String reg2,String reg3){
        this.mipsType=type;
        this.reg1=reg1;
        this.reg2=reg2;
        this.reg3=reg3;
    }
    public MipsInstruction(MipsType type,String reg1,String reg2){
        this.mipsType=type;
        this.reg1=reg1;
        this.reg2=reg2;
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
            if(this.memsize!=null){
                System.out.print(name+": .word ");
                Integer size= initParams.size();
                for (int i=0;i<size-1;i++){
                    System.out.print(initParams.get(i)+",");
                }
                System.out.println(initParams.get(size-1));
            }
            else System.out.println(name+": .word "+num);
        }
        else if (mipsType==MipsType.space){
            System.out.println(name+": .space"+memsize);
        }
        else if (mipsType==MipsType.func){
            System.out.println(name+":");
        }
        else{
            //如果是一个操作数
            if (reg2 == null){
                System.out.println(mipsType+" "+reg1);
            }//如果是两个操作数
            else if (reg3==null){
                System.out.println(mipsType+" "+reg1+" "+reg2);
            }//如果是三个操作数
            else{
                System.out.println(mipsType+" "+reg1+" "+reg2+" "+reg3);
            }
        }
    }
}
