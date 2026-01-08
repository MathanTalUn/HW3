package ast;
import types.*;
import ir.*;
import temp.*;
public class AstExpString extends AstExp {
	public String value;
	public AstExpString(String value, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.value = value;
	}
	public void printMe() {
		System.out.format("AST NODE STRING( %s )\n",value);
		AstGraphviz.getInstance().logNode(serialNumber, String.format("STRING\n%s",value.replace('"','\'')));
	}

	public Type semantMe() { return TypeString.getInstance(); }
	
	public Temp irMe() {
	    // Not needed for this exercise
	    return null;
	}
}