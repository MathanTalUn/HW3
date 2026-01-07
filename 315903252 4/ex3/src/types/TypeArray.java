package types;

public class TypeArray extends Type {
    public Type type; // The element type
    
    public TypeArray(Type type, String name) {
        this.type = type;
        this.name = name;
    }
    
    @Override
    public boolean isArray() { return true; }
    
    @Override
    public boolean isAssignableTo(Type target) {
        // Exact match (handles same named type)
        if (this == target) return true;
        
        // Target must be an array
        if (!target.isArray()) return false;
        
        TypeArray targetArr = (TypeArray) target;
        
        if ("anon_array".equals(this.name)) {
            return this.type == targetArr.type;
        }
        
        return false;
    }
}