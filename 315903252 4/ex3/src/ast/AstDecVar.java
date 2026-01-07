package ast;
import symboltable.*;
import types.*;

public class AstDecVar extends AstDec {
	public String type;
	public String name;
	public AstExp initialValue;
	
	public AstDecVar(String type, String name, AstExp initialValue, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.type = type;
		this.name = name;
		this.initialValue = initialValue;
	}

	public void printMe() {
		if (initialValue != null) System.out.format("VAR-DEC(%s):%s := initialValue\n",name,type);
		else System.out.format("VAR-DEC(%s):%s                \n",name,type);
		if (initialValue != null) initialValue.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, String.format("VAR\nDEC(%s)\n:%s",name,type));
		if (initialValue != null) AstGraphviz.getInstance().logEdge(serialNumber,initialValue.serialNumber);
	}

	public Type semantMe() {
		Type t = SymbolTable.getInstance().find(type);
		if (t == null || t.isVoid()) error("Invalid variable type " + type);
		
        // Check for duplicates in current scope
        if (SymbolTable.getInstance().containsInCurrentScope(name)) {
            error("Variable " + name + " already defined");
        }

		if (initialValue != null) {
            Type tInit = initialValue.semantMe();
            if (!tInit.isAssignableTo(t)) error("Type mismatch in init of " + name);
        }
		SymbolTable.getInstance().enter(name, t);
		return null;		
	}
}