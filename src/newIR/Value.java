package newIR;

import java.util.List;

public class Value {
    public String name;
    public ValueType valueType;
    public Integer constNum;
    public boolean isConst;
    public Integer onearrayNum=null;
    public Integer twoarrayNum=null;
    public List<Value> arrayNum;
    public String printArrayType() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (twoarrayNum != null) {
            sb.append(String.valueOf((onearrayNum * twoarrayNum)));
        } else {
            sb.append(onearrayNum);
        }
        sb.append(" x ");
        sb.append("i32]");
        return sb.toString();
    }
}
