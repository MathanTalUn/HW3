package ast;
import types.*;
import symboltable.*;

public class AstDecArray extends AstDec {
    public String name;
    public String typeName;
    
    public AstDecArray(String name, String typeName, int lineNumber) {
        super(lineNumber);
        this.name = name;
        this.typeName = typeName;
        serialNumber = AstNodeSerialNumber.getFresh();
    }

    public void printMe() {
        System.out.format("ARRAY TYPEDEF(%s) = %s[]\n", name, typeName);
        AstGraphviz.getInstance().logNode(serialNumber, String.format("ARRAY\n%s = %s[]", name, typeName));
    }

    public void semantSignature() {
        Type elementType = SymbolTable.getInstance().find(typeName);
        if (elementType == null || elementType.isVoid()) error("Array element type " + typeName + " invalid");
        TypeArray arType = new TypeArray(elementType, name);
        SymbolTable.getInstance().enter(name, arType);
    }

    public void semantBody() {
        // Nothing to check in body for typedef
    }

    public Type semantMe() {
        semantSignature();
        semantBody();
        return null;
    }
    public void irMe() {
    }
}