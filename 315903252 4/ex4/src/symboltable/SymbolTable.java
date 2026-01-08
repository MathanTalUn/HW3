package symboltable;
import java.util.HashMap;
import java.util.Stack;
import types.*;

public class SymbolTable {
    private static SymbolTable instance = null;
    
    private Stack<HashMap<String, Type>> scopes = new Stack<>();
    public TypeFunction currentFunction = null;
    
    protected SymbolTable() {
        scopes.push(new HashMap<>());
        
        enter("int", TypeInt.getInstance());
        enter("string", TypeString.getInstance());
        enter("void", TypeVoid.getInstance());
        
        enter("PrintInt", new TypeFunction(TypeVoid.getInstance(), "PrintInt", new TypeList(TypeInt.getInstance(), null)));
        enter("PrintString", new TypeFunction(TypeVoid.getInstance(), "PrintString", new TypeList(TypeString.getInstance(), null)));
    }

    public static SymbolTable getInstance() {
        if (instance == null) instance = new SymbolTable();
        return instance;
    }

    public void enter(String name, Type t) {
        scopes.peek().put(name, t);
    }

    public Type find(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return scopes.get(i).get(name);
            }
        }
        return null;
    }
    
    public boolean containsInCurrentScope(String name) {
        return scopes.peek().containsKey(name);
    }
    
    public boolean isInGlobalScope() {
        return scopes.size() == 1;
    }

    public void beginScope() {
        scopes.push(new HashMap<>());
    }

    public void endScope() {
        scopes.pop();
    }
}