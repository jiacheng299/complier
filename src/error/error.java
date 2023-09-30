package error;

import java.util.ArrayList;
import java.util.List;

public class error {
    private errorType type;
    private Integer errorLine;
    private static List<error>errorList=new ArrayList<error>();
    public error(error.errorType type, Integer errorLine) {
        this.type = type;
        this.errorLine = errorLine;
    }
    public static void addError(error.errorType type, Integer errorLine) {
        errorList.add(new error(type, errorLine));
    }
    public enum errorType{
        a,b,c,d,e,f,g,h,i,j,k,l,m;
    }



}
