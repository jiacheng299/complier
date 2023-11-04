package backend;

public class MyStack {
    public Integer index;
    public MyStack(Integer index){
        this.index=index;
    }

    public String getIndex() {
        return Integer.toString(index*4);
    }
}
