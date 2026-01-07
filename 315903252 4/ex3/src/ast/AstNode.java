package ast;
import types.Type;

public abstract class AstNode
{
	public int serialNumber;
	public int lineNumber;
	
	public AstNode(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public void printMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}
	
	public Type semantMe()
	{
		return null;
	}
    
    // Helper to throw the correctly formatted exception
    public void error(String msg) {
        System.out.format(">> ERROR [%d] %s\n", lineNumber, msg); // Print to console for debug
        throw new RuntimeException("ERROR(" + lineNumber + ")");   // Throw for Main to catch
    }
}