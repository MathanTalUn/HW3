package ast;
import symboltable.*;
import types.*;
import ir.*;
import temp.*;

public class AstExpVarSimple extends AstExpVar {
	public String name;
	
	public AstExpVarSimple(String name, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		System.out.format("====================== var -> ID( %s )\n",name);
		this.name = name;
	}

	public void printMe() {
		System.out.format("AST NODE SIMPLE VAR( %s )\n",name);
		AstGraphviz.getInstance().logNode(serialNumber, String.format("SIMPLE\nVAR\n(%s)",name));
	}

	public Type semantMe() {
        Type t = SymbolTable.getInstance().find(name);
        if (t == null) error("Variable " + name + " not found");
		return t;
	}
    
    public Temp irMe() {
         Temp t = IrSymbolTable.getInstance().find(name);
         if (t != null) return t;
         
         // Global
         t = TempFactory.getInstance().getFreshTEMP();
         Ir.getInstance().AddIrCommand(new IrCommandLoad(t, name));
         return t;
    }
}