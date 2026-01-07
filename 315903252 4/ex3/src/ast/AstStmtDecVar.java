package ast;
import types.*;
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
}