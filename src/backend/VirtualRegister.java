package backend;

public class VirtualRegister {
    public boolean free=true;
    public String name="";
    public VirtualRegister(){}
    public VirtualRegister(String name){
        this.name=name;
        this.free=false;
    }
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VirtualRegister &&
                this.name.equals(((VirtualRegister) obj).name);
    }
}
