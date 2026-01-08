package ast;
import types.*;
import ir.*;
import temp.*;

public class AstExpVoid extends AstExp {
    public AstExpVoid(int lineNumber) { super(lineNumber); serialNumber = AstNodeSerialNumber.getFresh(); }
    public void printMe() { System.out.format("NIL\n"); }

    public Type semantMe() { return TypeVoid.getInstance(); }
    public Temp irMe() { return TempFactory.getInstance().getDummyTEMP(); }
}