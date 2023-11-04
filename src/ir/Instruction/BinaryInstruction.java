package ir.Instruction;

import ir.User;
import ir.Value;

public class BinaryInstruction extends BaseInstruction{
        public OpCode opCode;
        public BinaryInstruction(Value value1, Value value2, User result,OpCode opCode) {
            this.value1 = value1;
            this.value2 = value2;
            this.result = result;
            this.opCode=opCode;
        }
        public void print(){
            System.out.println(this.result.getName()+" = "+this.opCode+" "+value1.getType()+" "+value1.getName()+", "+value2.getName());
        }
}
