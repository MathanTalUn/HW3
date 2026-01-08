package ast;
import types.*;
import ir.*;

public class AstDecList extends AstNode {
	public AstDec head;
	public AstDecList tail;

	public AstDecList(AstDec head, AstDecList tail, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.head = head;
		this.tail = tail;
	}

	public void printMe() {
		System.out.print("AST NODE DEC LIST\n");
		if (head != null) head.printMe();
		if (tail != null) tail.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, "DEC\nLIST\n");
		if (head != null) AstGraphviz.getInstance().logEdge(serialNumber,head.serialNumber);
		if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber,tail.serialNumber);
	}

	public void semantSignature() {
		if (head != null) head.semantSignature();
		if (tail != null) tail.semantSignature();
	}
	
	public void semantBody() {
		if (head != null) head.semantBody();
		if (tail != null) tail.semantBody();
	}

	public Type semantMe() {
		semantSignature();
		semantBody();
		return null;
	}
	
	public void irMe() {
	    irGlobals();
	    irFunctions();
	}
	
	public void irGlobals() {
	    if (head != null && head instanceof AstDecVar) head.irMe();
	    if (tail != null) tail.irGlobals();
	}
	
	public void irFunctions() {
	    if (head != null && head instanceof AstDecFunc) head.irMe();
	    if (tail != null) tail.irFunctions();
	}
}