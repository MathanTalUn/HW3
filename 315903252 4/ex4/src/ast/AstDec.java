package ast;
import ir.*;

public abstract class AstDec extends AstNode {
    public AstDec(int lineNumber) { super(lineNumber); }
    public abstract void irMe();
    
    public abstract void semantSignature();
    public abstract void semantBody();
}