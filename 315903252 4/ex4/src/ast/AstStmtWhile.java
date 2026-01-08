package ast;
import symboltable.*;
import types.*;
import ir.*;
import temp.*;

public class AstStmtWhile extends AstStmt {
	public AstExp cond;
	public AstStmtList body;
	public AstStmtWhile(AstExp cond, AstStmtList body, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.cond = cond;
		this.body = body;
	}
    public void printMe() {
		System.out.print("AST NODE STMT WHILE\n");
		if (cond != null) cond.printMe();
		if (body != null) body.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, "WHILE (left)\nDO right");
		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber,cond.serialNumber);
		if (body != null) AstGraphviz.getInstance().logEdge(serialNumber,body.serialNumber);
	}
    public Type semantMe() {
		if (cond.semantMe() != TypeInt.getInstance()) error("condition inside WHILE is not integral");
		SymbolTable.getInstance().beginScope();
		if (body != null) body.semantMe();
		SymbolTable.getInstance().endScope();
		return null;		
	}
	
	public void irMe() {
	    String L_TEST = IrCommand.getFreshLabel("While_Test");
	    String L_EXIT = IrCommand.getFreshLabel("While_Exit");
	    
	    Ir.getInstance().AddIrCommand(new IrCommandLabel(L_TEST));
	    
	    Temp t = cond.irMe();
	    Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(t, L_EXIT));
	    
	    IrSymbolTable.getInstance().beginScope();
	    if (body != null) body.irMe();
	    IrSymbolTable.getInstance().endScope();
	    
	    Ir.getInstance().AddIrCommand(new IrCommandJumpLabel(L_TEST));
	    Ir.getInstance().AddIrCommand(new IrCommandLabel(L_EXIT));
	}
}