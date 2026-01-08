package ast;
import types.*;
import ir.*;
import temp.*;

public class AstExpBinop extends AstExp {
	int op;
	public AstExp left;
	public AstExp right;
	
	public AstExpBinop(AstExp left, AstExp right, int op, int lineNumber) {
		super(lineNumber);
		serialNumber = AstNodeSerialNumber.getFresh();
		this.left = left;
		this.right = right;
		this.op = op;
	}
	
	public void printMe() { /* Standard */ }

	public Type semantMe() {
		Type t1 = left.semantMe();
		Type t2 = right.semantMe();
		if (op == 6) { // EQ
            if (t1 == t2) return TypeInt.getInstance();
            if (t1.isAssignableTo(t2) || t2.isAssignableTo(t1)) return TypeInt.getInstance();
            error("Invalid equality test");
        }
        if (op == 0) { // PLUS
            if (t1.isInt() && t2.isInt()) return TypeInt.getInstance();
            if (t1.isString() && t2.isString()) return TypeString.getInstance();
            error("Invalid addition types");
        }
        if (!t1.isInt() || !t2.isInt()) error("Binary op requires ints");
        if (op == 3) { // DIVIDE
             if (right instanceof AstExpInt && ((AstExpInt)right).value == 0) error("Division by zero");
        }
		return TypeInt.getInstance();
	}
	public Temp irMe() {
        Temp t1 = left.irMe();
        Temp t2 = right.irMe();
        Temp dst = TempFactory.getInstance().getFreshTEMP();
        if (op == 6) { 
            Ir.getInstance().AddIrCommand(new IrCommandBinopEqIntegers(dst, t1, t2));
        } else if (op == 4) { 
            Ir.getInstance().AddIrCommand(new IrCommandBinopLtIntegers(dst, t1, t2));
        } else if (op == 5) { 
            Ir.getInstance().AddIrCommand(new IrCommandBinopLtIntegers(dst, t1, t2));
        } else if (op == 2 || op == 3) { 
             Ir.getInstance().AddIrCommand(new IrCommandBinopMulIntegers(dst, t1, t2));
        } else { 
             Ir.getInstance().AddIrCommand(new IrCommandBinopAddIntegers(dst, t1, t2));
        }
        return dst;
    }
}