package ast;

import symboltable.*;
import types.*;
import ir.*;
import temp.*;

public class AstStmtIf extends AstStmt {
	public AstExp cond;
	public AstStmtList body;
	public AstStmtList elseBody;

	public AstStmtIf(AstExp cond, AstStmtList body, AstStmtList elseBody, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.cond = cond;
		this.body = body;
		this.elseBody = elseBody;
	}

	public void printMe() {
		System.out.print("AST NODE STMT IF\n");
		if (cond != null)
			cond.printMe();
		if (body != null)
			body.printMe();
		if (elseBody != null)
			elseBody.printMe();

		AstGraphviz.getInstance().logNode(serialNumber,
				"IF (left)\nTHEN right\n" + (elseBody != null ? "ELSE next" : ""));
		if (cond != null)
			AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
		if (body != null)
			AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
		if (elseBody != null)
			AstGraphviz.getInstance().logEdge(serialNumber, elseBody.serialNumber);
	}

	public Type semantMe() {
		if (cond.semantMe() != TypeInt.getInstance())
			error("condition inside IF is not integral");
		SymbolTable.getInstance().beginScope();
		if (body != null)
			body.semantMe();
		SymbolTable.getInstance().endScope();

		if (elseBody != null) {
			SymbolTable.getInstance().beginScope();
			elseBody.semantMe();
			SymbolTable.getInstance().endScope();
		}

		return null;
	}
	public void irMe() {
	    String L_FALSE = IrCommand.getFreshLabel("If_False");
	    String L_EXIT = IrCommand.getFreshLabel("If_Exit");
	
	    Temp t = cond.irMe();
	    Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(t, L_FALSE));
	
	    IrSymbolTable.getInstance().beginScope();
	    if (body != null) body.irMe();
	    IrSymbolTable.getInstance().endScope();
	    Ir.getInstance().AddIrCommand(new IrCommandJumpLabel(L_EXIT));
	
	    Ir.getInstance().AddIrCommand(new IrCommandLabel(L_FALSE));
	    if (elseBody != null) {
	        IrSymbolTable.getInstance().beginScope();
	        elseBody.irMe();
	        IrSymbolTable.getInstance().endScope();
	    }
	
	    Ir.getInstance().AddIrCommand(new IrCommandLabel(L_EXIT));
	}
}