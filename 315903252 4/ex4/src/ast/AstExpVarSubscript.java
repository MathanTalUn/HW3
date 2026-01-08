package ast;
import types.*;
import ir.*;
import temp.*;

public class AstExpVarSubscript extends AstExpVar {
	public AstExpVar var;
	public AstExp subscript;
	
	public AstExpVarSubscript(AstExpVar var, AstExp subscript, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = var;
		this.subscript = subscript;
	}
	public void printMe() {
		System.out.print("AST NODE SUBSCRIPT VAR\n");
		if (var != null) var.printMe();
		if (subscript != null) subscript.printMe();
	}

    public Type semantMe() {
        Type t = var.semantMe();
        if (t instanceof TypeArray) {
            if (subscript.semantMe() != TypeInt.getInstance()) error("Array index must be int");
            return ((TypeArray)t).type;
        }
        error("Subscript on non-array");
        return null;
    }
	
	public Temp irMe() {
        return TempFactory.getInstance().getDummyTEMP();
	}
}