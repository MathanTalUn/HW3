package ast;
import types.*;
import ir.*;
import temp.*;

public class AstExpVarField extends AstExpVar {
	public AstExpVar var;
	public String fieldName;
	
	public AstExpVarField(AstExpVar var, String fieldName, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.var = var;
		this.fieldName = fieldName;
	}
	public void printMe() {
		System.out.format("FIELD\nNAME\n(___.%s)\n",fieldName);
		if (var != null) var.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, String.format("FIELD\nVAR\n___.%s",fieldName));
		if (var  != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
	}

	public Type semantMe() {
		Type t = var.semantMe();
        if (t instanceof TypeClass) {
            Type ft = ((TypeClass)t).getFieldType(fieldName);
            if (ft != null) return ft;
        }
		error("Field " + fieldName + " not found");
		return null;
	}
	
	public Temp irMe() {
        return TempFactory.getInstance().getDummyTEMP();
	}
}