package ast;
import temp.*;
import ir.*;

public abstract class AstExp extends AstNode {
    public AstExp(int lineNumber) { super(lineNumber); }
    public abstract Temp irMe();
}