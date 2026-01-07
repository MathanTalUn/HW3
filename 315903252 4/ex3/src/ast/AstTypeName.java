package ast;
import symboltable.*;
import types.*;

public class AstTypeName extends AstNode {
	public String type;
	public String name;
	
	public AstTypeName(String type, String name, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.type = type;
		this.name = name;
	}

	public void printMe() {
		System.out.format("NAME(%s):TYPE(%s)\n",name,type);
		AstGraphviz.getInstance().logNode(serialNumber, String.format("NAME:TYPE\n%s:%s",name,type));
	}

	public Type semantMe() {
		Type t = SymbolTable.getInstance().find(type);
		if (t == null) error("Undeclared type " + type);
        
        // Check for duplicates in current scope
        if (SymbolTable.getInstance().containsInCurrentScope(name)) {
            error("Variable " + name + " already defined");
        }
        
		SymbolTable.getInstance().enter(name,t);
		return t;
	}	
}