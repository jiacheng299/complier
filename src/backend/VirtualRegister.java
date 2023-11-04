package backend;

public class VirtualRegister {
    public boolean free=true;
    public String name;
    public VirtualRegister(String name){
        this.name=name;
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
