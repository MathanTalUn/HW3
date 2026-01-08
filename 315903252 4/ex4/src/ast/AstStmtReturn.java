package ast;

import symboltable.*;
import types.*;
import ir.*;
import temp.*;

public class AstStmtReturn extends AstStmt {
	public AstExp exp;

	public AstStmtReturn(AstExp exp, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.exp = exp;
	}

	public void printMe() {
		System.out.print("AST NODE STMT RETURN\n");
		if (exp != null)
			exp.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, "RETURN");
		if (exp != null)
			AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
	}

	public Type semantMe() {
		TypeFunction curr = SymbolTable.getInstance().currentFunction;

		if (curr.returnType.isVoid()) {
			if (exp != null)
				error("Return statement in void function cannot have a return value");
		} else {
			Type actual = (exp == null) ? TypeVoid.getInstance() : exp.semantMe();
			if (!actual.isAssignableTo(curr.returnType))
				error("Return type mismatch");
		}
		return null;
	}
	
	public void irMe() {
	    // NOT RELEVANT FOR THIS EXERCISE
	    if (exp != null) exp.irMe();
	}
}