package ast;
import symboltable.*;
import types.*;

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
		if (cond != null) cond.printMe();
		if (body != null) body.printMe();
        if (elseBody != null) elseBody.printMe();
        
		AstGraphviz.getInstance().logNode(serialNumber, "IF (left)\nTHEN right" + (elseBody != null ? "\nELSE" : ""));
		if (cond != null) AstGraphviz.getInstance().logEdge(serialNumber,cond.serialNumber);
		if (body != null) AstGraphviz.getInstance().logEdge(serialNumber,body.serialNumber);
        if (elseBody != null) AstGraphviz.getInstance().logEdge(serialNumber,elseBody.serialNumber);
	}
	public Type semantMe() {
		if (cond.semantMe() != TypeInt.getInstance()) error("condition inside IF is not integral");
		
        SymbolTable.getInstance().beginScope();
		if (body != null) body.semantMe();
		SymbolTable.getInstance().endScope();
        
        if (elseBody != null) {
            SymbolTable.getInstance().beginScope();
            elseBody.semantMe();
            SymbolTable.getInstance().endScope();
        }
		return null;		
	}	
}