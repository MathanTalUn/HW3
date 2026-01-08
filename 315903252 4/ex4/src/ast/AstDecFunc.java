package ast;
import symboltable.*;
import types.*;
import ir.*;

public class AstDecFunc extends AstDec {
	public String returnTypeName;
	public String name;
	public AstTypeNameList params;
	public AstStmtList body;
	
	public AstDecFunc(String returnTypeName, String name, AstTypeNameList params, AstStmtList body, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.returnTypeName = returnTypeName;
		this.name = name;
		this.params = params;
		this.body = body;
	}

	public void printMe() {
		System.out.format("FUNC(%s):%s\n",name,returnTypeName);
		if (params != null) params.printMe();
		if (body   != null) body.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, String.format("FUNC(%s)\n:%s\n",name,returnTypeName));
		if (params != null) AstGraphviz.getInstance().logEdge(serialNumber,params.serialNumber);
		if (body   != null) AstGraphviz.getInstance().logEdge(serialNumber,body.serialNumber);
	}

    private TypeList getSignature(AstTypeNameList params) {
        if (params == null) return null;
        Type t = SymbolTable.getInstance().find(params.head.type);
        if (t == null) error("Non existing type " + params.head.type);
        return new TypeList(t, (params.tail == null) ? null : getSignature(params.tail));
    }

	public void semantSignature() {
		Type returnType = SymbolTable.getInstance().find(returnTypeName);
		if (returnType == null) error("Non existing return type " + returnTypeName);
        
        if (SymbolTable.getInstance().isInGlobalScope()) {
             if (SymbolTable.getInstance().find(name) != null) error("Function " + name + " already declared");
        }

        TypeList signature = (params != null) ? getSignature(params) : null;
        TypeFunction funcType = new TypeFunction(returnType, name, signature);
        SymbolTable.getInstance().enter(name, funcType);
	}

	public void semantBody() {
		TypeFunction funcType = (TypeFunction) SymbolTable.getInstance().find(name);
		
		SymbolTable.getInstance().beginScope();
        if (params != null) params.semantList();
        SymbolTable.getInstance().currentFunction = funcType;
		if (body != null) body.semantMe();
        SymbolTable.getInstance().currentFunction = null;
		SymbolTable.getInstance().endScope();
	}

	public Type semantMe() {
		semantSignature();
		semantBody();
		return null;		
    }
    
    public void irMe() {
        if (name.equals("main")) {
            IrSymbolTable.getInstance().beginScope();
            if (body != null) body.irMe();
            IrSymbolTable.getInstance().endScope();
        }
    }
}