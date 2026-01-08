package ast;
import types.*;
import ir.*;
import temp.*;
public class AstStmtDecVar extends AstStmt {
	public AstDecVar var;
	public AstStmtDecVar(AstDecVar var, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = var;
	}
	public void printMe() {
		var.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, String.format("STMT\nDEC\nVAR"));
		AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
	}

	public Type semantMe() { return var.semantMe(); }
	
	public void irMe() {
	    Temp t = TempFactory.getInstance().getFreshTEMP();
	    IrSymbolTable.getInstance().enter(var.name, t);
	    
	    if (var.initialValue != null) {
	        Temp t_init = var.initialValue.irMe();
	        Ir.getInstance().AddIrCommand(new IrCommandMove(t, t_init));
	    }
	}
}