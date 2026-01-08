package ast;
import ir.*;

public abstract class AstStmt extends AstNode {
    public AstStmt(int lineNumber) { super(lineNumber); }
    public abstract void irMe();
}