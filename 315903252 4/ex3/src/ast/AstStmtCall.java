package ast;
import types.*;

public class AstStmtCall extends AstStmt {
	public AstExpCall callExp;
	
	public AstStmtCall(AstExpCall callExp, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.callExp = callExp;
	}
	
	public void printMe() {
		callExp.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, String.format("STMT\nCALL"));
		AstGraphviz.getInstance().logEdge(serialNumber,callExp.serialNumber);
	}
    
    public Type semantMe() {
        if (callExp != null) callExp.semantMe();
        return null;
    }
}