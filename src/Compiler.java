// 按两次 Shift 打开“随处搜索”对话框并输入 `show whitespaces`，
// 然后按 Enter 键。现在，您可以在代码中看到空格字符。

import front.Lexer;
import front.Parser;
import front.Token;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Compiler {
    public static void main(String[] args) throws IOException {
        // 当文本光标位于高亮显示的文本处时按 Alt+Enter，
        // 可查看 IntelliJ IDEA 对于如何修正该问题的建议。
        Scanner scanner=new Scanner(System.in);
        BufferedReader reader=new BufferedReader(new FileReader("testfile.txt"));
        StringBuilder content=new StringBuilder();
        String line;
        while((line=reader.readLine())!=null){
            content.append(line);
            content.append(System.lineSeparator());
        }
        Lexer lexer=new Lexer(content.toString());
        System.out.println(content);
        List<Token> words=lexer.tokenize();


        try (PrintWriter writer = new PrintWriter(new FileWriter("output.txt"))) {
            for (int i = 0; i < words.size(); i++) {
                writer.println(words.get(i));
            }
        }
        Parser parser=new Parser(words);
        parser.parse();
        parser.getEntrance().print();
    }
}