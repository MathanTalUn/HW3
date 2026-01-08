package ir;
import java.util.HashMap;
import java.util.Stack;
import temp.Temp;

public class IrSymbolTable {
    private static IrSymbolTable instance = null;
    
    // Map variable name to TEMP
    private Stack<HashMap<String, Temp>> scopes = new Stack<>();
    
    // Map TEMP to variable name (for reporting)
    public HashMap<Temp, String> tempToName = new HashMap<>();

    protected IrSymbolTable() {
        scopes.push(new HashMap<>());
    }

    public static IrSymbolTable getInstance() {
        if (instance == null) instance = new IrSymbolTable();
        return instance;
    }
    
    public void reset() {
        scopes.clear();
        scopes.push(new HashMap<>());
        tempToName.clear();
    }

    public void enter(String name, Temp t) {
        scopes.peek().put(name, t);
        tempToName.put(t, name);
    }

    public Temp find(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return scopes.get(i).get(name);
            }
        }
        return null; // Should not happen if Semantic Analysis passed
    }

    public void beginScope() {
        scopes.push(new HashMap<>());
    }

    public void endScope() {
        scopes.pop();
    }
    
    public boolean isGlobalScope() {
        return scopes.size() == 1;
    }
    
    public String getName(Temp t) {
        return tempToName.get(t);
    }
}
