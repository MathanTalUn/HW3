/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

public class IrCommandJumpIfEqToZero extends IrCommand
{
	public Temp t;
	public String label;
	
	public IrCommandJumpIfEqToZero(Temp t, String label)
	{
		this.t          = t;
		this.label = label;
	}
}
