package node;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

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
