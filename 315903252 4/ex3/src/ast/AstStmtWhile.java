package ast;
import symboltable.*;
import types.*;

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
}