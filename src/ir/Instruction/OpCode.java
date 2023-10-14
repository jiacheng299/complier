package ir.Instruction;

import Token.TokenType;

public enum OpCode {
    add, sub, mul, sdiv, mod;
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
            // 其他类型的运算符对应的 OpCode
            default:
                throw new IllegalArgumentException("Invalid token type: " + tokenType);
        }
    }
}
