package ast;
import symboltable.*;
import types.*;

public class AstExpCall extends AstExp {
	public String funcName;
	public AstExpList args;
    public AstExpVar var;

	public AstExpCall(AstExpVar var, String funcName, AstExpList args, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
		this.funcName = funcName;
		this.args = args;
	}

	public void printMe() { /* Standard */ }

	public Type semantMe() {
        TypeFunction f = null;
        if (var == null) {
            Type t = SymbolTable.getInstance().find(funcName);
            if (t instanceof TypeFunction) f = (TypeFunction) t;
            else error("Function " + funcName + " not found");
        } else {
            Type t = var.semantMe();
            if (t instanceof TypeClass) f = ((TypeClass)t).getMethod(funcName);
            if (f == null) error("Method " + funcName + " not found in variable");
        }
        TypeList p = f.params;
        AstExpList a = args;
        while (p != null && a != null) {
            if (!a.head.semantMe().isAssignableTo(p.head)) error("Argument mismatch for " + funcName);
            p = p.tail; a = a.tail;
        }
        if (p != null || a != null) error("Argument count mismatch for " + funcName);
		return f.returnType;
	}
}