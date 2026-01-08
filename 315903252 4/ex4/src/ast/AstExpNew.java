package ast;
import types.*;
import types.*;
import ir.*;
import symboltable.*;
import temp.*;

public class AstExpNew extends AstExp {
    public String typeName;
    public AstExp sizeExp;
    
    public AstExpNew(String typeName, AstExp sizeExp, int lineNumber) {
        super(lineNumber);
        this.typeName = typeName;
        this.sizeExp = sizeExp;
        serialNumber = AstNodeSerialNumber.getFresh();
    }

    public void printMe() {
        if (sizeExp != null) { System.out.format("NEW %s[...]\n", typeName); sizeExp.printMe(); } 
        else { System.out.format("NEW %s\n", typeName); }
        AstGraphviz.getInstance().logNode(serialNumber, String.format("NEW %s", typeName));
        if (sizeExp != null) AstGraphviz.getInstance().logEdge(serialNumber, sizeExp.serialNumber);
    }

    public Type semantMe() {
        Type t = SymbolTable.getInstance().find(typeName);
        if (t == null) error("New on non-existing type " + typeName);
        if (sizeExp != null) {
            if (sizeExp.semantMe() != TypeInt.getInstance()) error("Array size must be int");
            if (t.isVoid()) error("Cannot create array of void");
            return new TypeArray(t, "anon_array");
        } else {
            if (!t.isClass()) error("Cannot instantiate non-class type " + typeName);
            return t;
        }
    }

	public Temp irMe() {
        return TempFactory.getInstance().getDummyTEMP();
	}
}