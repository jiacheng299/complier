package ir;

public class Instruction {
    private OpCode opCode;
    private Integer id;
    public StringBuilder str;
    private String type;
    private BasicBlock basicBlock;
    private Value returnValue;
    private  Value value1;
    private Value value2;
    public Instruction(String type,Integer id,BasicBlock basicBlock) {
        this.type = type;
        this.id = id;
        this.basicBlock=basicBlock;
    }
    public Instruction(String type,Integer id,Value returnValue,BasicBlock basicBlock) {
        this.type = type;
        this.id = id;
        this.returnValue = returnValue;
        this.basicBlock=basicBlock;
    }
    public Instruction(String type,OpCode opCode, Integer id, Value value1, Value value2,BasicBlock basicBlock) {
        this.type = type;
        this.opCode = opCode;
        this.id = id;
        this.value1 = value1;
        this.value2 = value2;
        this.basicBlock=basicBlock;
    }
    public void print(){
        if (type.equals("return")){
            //还需要加入一个返回值的判断
            System.out.println("ret i32");
        }
        else{
            System.out.println("%"+id + " = "+opCode+" i32");
        }
    }

}

enum OpCode {
    add,sub,mul,sdiv,mod;
}