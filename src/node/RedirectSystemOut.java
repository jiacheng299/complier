package node;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class RedirectSystemOut {
    public static PrintStream ps;
    public static PrintStream ir;
    public static PrintStream mips;
    static {
        try {
            ps = new PrintStream("output.txt");
            ir = new PrintStream("llvm_ir.txt");
            mips=new PrintStream("mips.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public RedirectSystemOut() throws FileNotFoundException {
    }


}
