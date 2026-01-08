package analysis;

import ir.Ir;
import ir.IrSymbolTable;
import java.io.PrintWriter;
import java.util.*;

public class AnalysisDriver {
    public static void run(PrintWriter fileWriter) {
        try {
            // Build CFG
            CFG cfg = CFG.build(Ir.getInstance());
            
            // Run Analysis
            UninitializedVariableAnalysis analysis = new UninitializedVariableAnalysis(cfg, IrSymbolTable.getInstance().tempToName);
            Set<String> errors = analysis.analyze();
            
            // Output Results
            List<String> sortedErrors = new ArrayList<>(errors);
            Collections.sort(sortedErrors);
            
            if (sortedErrors.isEmpty()) {
                fileWriter.print("!OK");
            } else {
                for (int i = 0; i < sortedErrors.size(); i++) {
                    fileWriter.print(sortedErrors.get(i));
                    if (i < sortedErrors.size() - 1) {
                        fileWriter.println();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
