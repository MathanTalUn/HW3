package ast;
public class AstExpList extends AstNode {
	public AstExp head;
	public AstExpList tail;
	public AstExpList(AstExp head, AstExpList tail, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.head = head;
		this.tail = tail;
	}
	public void printMe() {
		System.out.print("AST NODE EXP LIST\n");
		if (head != null) head.printMe();
		if (tail != null) tail.printMe();
		AstGraphviz.getInstance().logNode(serialNumber, "EXP\nLIST\n");
		if (head != null) AstGraphviz.getInstance().logEdge(serialNumber,head.serialNumber);
		if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber,tail.serialNumber);
	}
}