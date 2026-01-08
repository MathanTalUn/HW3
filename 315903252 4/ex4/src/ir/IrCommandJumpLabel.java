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

public class IrCommandJumpLabel extends IrCommand
{
	public String label;
	
	public IrCommandJumpLabel(String label)
	{
		this.label = label;
	}
}
