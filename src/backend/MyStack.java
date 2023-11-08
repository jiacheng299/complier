package backend;

public class MyStack {
    public Integer index;
    public boolean isArray=false;
    public MyStack(Integer index){
        this.index=index;
    }

    public String getIndex() {
        return Integer.toString(index*4);
    }
}
