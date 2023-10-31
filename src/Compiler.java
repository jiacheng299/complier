// 按两次 Shift 打开“随处搜索”对话框并输入 `show whitespaces`，
// 然后按 Enter 键。现在，您可以在代码中看到空格字符。

import config.config;
import error.*;
import front.ErrorHandle;
import front.Lexer;
import front.Parser;
import Token.Token;
import ir.Generator;
import ir.Module;
import node.RedirectSystemOut;

import java.io.*;
import java.util.Comparator;
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

        if (config.showLexer){
            try (PrintWriter writer = new PrintWriter(new FileWriter("tokens.txt"))) {
                for (int i = 0; i < words.size(); i++) {
                    writer.println(words.get(i));
                }
            }
        }
        Parser parser=new Parser(words);
        parser.parse();
        if (config.showParser){
            parser.getEntrance().print();
        }
        ErrorHandle errorHandle=ErrorHandle.getInstance();
        errorHandle.start(parser.getEntrance());
        if (config.showError){
            List<error> errorList=error.getErrorList();
            errorList.sort(Comparator.comparingInt(error::getErrorLine));
            try(PrintWriter writer = new PrintWriter(new FileWriter("error.txt"))){

                for (int i=0;i<errorList.size(); i++){
                    writer.println(errorList.get(i));
                }
            }
        }
        System.setOut(RedirectSystemOut.ir);
        if (config.generator){
            Generator generator=new Generator();
            generator.start(parser.getEntrance());
            Module.getModule().print();
        }

    }
}