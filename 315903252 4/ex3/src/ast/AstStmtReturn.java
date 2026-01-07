package ast;
import symboltable.*;
import types.*;

public class AstStmtReturn extends AstStmt {
	public AstExp exp;
	public AstStmtReturn(AstExp exp, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.exp = exp;
	}
	public void printMe() {
		System.out.print("AST NODE STMT RETURN\n");
		if (exp != null) exp.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, "RETURN");
		if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
	}
    public Type semantMe() {
        TypeFunction curr = SymbolTable.getInstance().currentFunction;
        if (curr == null) error("Return statement outside function");

        if (curr.returnType.isVoid()) {
            if (exp != null) error("Return statement in void function must be empty");
        } else {
            if (exp == null) error("Return statement missing value");
            Type actual = exp.semantMe();
            if (!actual.isAssignableTo(curr.returnType)) error("Return type mismatch");
        }
        return null;
    }
}