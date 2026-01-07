package ast;

import java.io.PrintWriter;

public class AstGraphviz
{
	/***********************/
	/* The file writer ... */
	/***********************/
	private PrintWriter fileWriter;
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static AstGraphviz instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	private AstGraphviz() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static AstGraphviz getInstance()
	{
		if (instance == null)
		{
			instance = new AstGraphviz();
			
			/****************************/
			/* Initialize a file writer */
			/****************************/
			try
			{
				String dirname="./output/";
				String filename="AST_IN_GRAPHVIZ_DOT_FORMAT.txt";
				instance.fileWriter = new PrintWriter(dirname+filename);
                
                /******************************************************/
                /* Print Directed Graph header in Graphviz dot format */
                /******************************************************/
                instance.fileWriter.print("digraph\n");
                instance.fileWriter.print("{\n");
                instance.fileWriter.print("graph [ordering = \"out\"]\n");
			}
			catch (Exception e)
			{
				// If we can't write the graph (e.g. directory missing), 
                // just print trace and continue. Don't crash the compiler.
				e.printStackTrace();
                instance.fileWriter = null;
			}
		}
		return instance;
	}

	/***********************************/
	/* Log node in graphviz dot format */
	/***********************************/
	public void logNode(int nodeSerialNumber,String nodeName)
	{
        if (fileWriter == null) return; // robustness check
        
		fileWriter.format(
			"v%d [label = \"%s\"];\n",
			nodeSerialNumber,
			nodeName);
	}

	/***********************************/
	/* Log edge in graphviz dot format */
	/***********************************/
	public void logEdge(
		int fatherNodeSerialNumber,
		int sonNodeSerialNumber)
	{
        if (fileWriter == null) return; // robustness check
        
		fileWriter.format(
			"v%d -> v%d;\n",
			fatherNodeSerialNumber,
			sonNodeSerialNumber);
	}
	
	/******************************/
	/* Finalize graphviz dot file */
	/******************************/
	public void finalizeFile()
	{
        if (fileWriter == null) return; // robustness check
        
		fileWriter.print("}\n");
		fileWriter.close();
	}
}