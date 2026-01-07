package ast;
import types.*;
public class AstStmtAssign extends AstStmt {
	public AstExpVar var;
	public AstExp exp;
	public AstStmtAssign(AstExpVar var, AstExp exp, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = var;
		this.exp = exp;
	}
	public void printMe() {
		System.out.print("AST NODE ASSIGN STMT\n");
		if (var != null) var.printMe();
		if (exp != null) exp.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, "ASSIGN\nleft := right\n");
		AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
		AstGraphviz.getInstance().logEdge(serialNumber,exp.serialNumber);
	}
	public Type semantMe() {
        Type t1 = var.semantMe();
        Type t2 = exp.semantMe();
        if (!t2.isAssignableTo(t1)) error("Type mismatch in assignment");
		return null;
	}
}