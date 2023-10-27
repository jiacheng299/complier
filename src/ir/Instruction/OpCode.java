package ir.Instruction;

import Token.TokenType;

public enum OpCode {
    add, sub, mul, sdiv, mod,ne,eql,sgt,sge,slt,sle;
    public static OpCode Token2Op(TokenType tokenType) {
        switch (tokenType) {
            case PLUS:
                return OpCode.add;
            case MINU:
                return OpCode.sub;
            case MULT:
                return OpCode.mul;
            case MOD:
                return OpCode.mod;
            case DIV:
                return OpCode.sdiv;
            case EQL:
                return OpCode.eql;
            case NEQ:
                return OpCode.ne;
            // 其他类型的运算符对应的 OpCode
            case GRE:
                return sgt;
            case LSS:
                return slt;
            case GEQ:
                return sge;
            case LEQ:
                return sle;
            default:
                throw new IllegalArgumentException("Invalid token type: " + tokenType);
        }
    }
}
