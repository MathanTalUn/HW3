package ast;
import types.*;
import ir.*;
import temp.*;

public class AstExpInt extends AstExp {
	public int value;
	public AstExpInt(int value, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.value = value;
	}
	public void printMe() {
		System.out.format("AST NODE INT( %d )\n",value);
		AstGraphviz.getInstance().logNode(serialNumber, String.format("INT(%d)",value));
	}
	public Type semantMe() { return TypeInt.getInstance(); }
	
	public Temp irMe() {
		Temp t = TempFactory.getInstance().getFreshTEMP();
		Ir.getInstance().AddIrCommand(new IRcommandConstInt(t, value));
		return t;
	}
}