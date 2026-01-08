package ir;
import temp.*;

public class IrCommandMove extends IrCommand {
    public Temp dst;
    public Temp src;
    
    public IrCommandMove(Temp dst, Temp src) {
        this.dst = dst;
        this.src = src;
    }
}
