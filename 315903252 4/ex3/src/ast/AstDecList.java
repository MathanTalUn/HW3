package ast;
import types.*;

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

	public Type semantMe() {
		if (head != null) head.semantMe();
		if (tail != null) tail.semantMe();
		return null;
	}
}