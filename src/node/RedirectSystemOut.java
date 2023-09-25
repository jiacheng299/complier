package node;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class RedirectSystemOut {
    public static PrintStream ps;

    static {
        try {
            ps = new PrintStream("output.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public RedirectSystemOut() throws FileNotFoundException {
    }


}
