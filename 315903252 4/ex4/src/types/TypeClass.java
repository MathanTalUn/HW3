package types;
import java.util.ArrayList;
import java.util.List;

public class TypeClass extends Type
{
	public TypeClass father;
    public List<TypeClassVarDec> fields = new ArrayList<>();
    public List<TypeFunction> methods = new ArrayList<>();
	
	public TypeClass(TypeClass father, String name)
	{
		this.name = name;
		this.father = father;
	}
    
    @Override
    public boolean isClass() { return true; }

    @Override
    public boolean isAssignableTo(Type target) {
        if (this == target) return true;
        if (target.isClass()) {
            // Check inheritance chain
            TypeClass curr = this;
            while (curr != null) {
                if (curr == target) return true;
                curr = curr.father;
            }
        }
        return false;
    }
    
    public Type getFieldType(String fieldName) {
        for (TypeClassVarDec f : fields) {
            if (f.name.equals(fieldName)) return f.t;
        }
        if (father != null) return father.getFieldType(fieldName);
        return null;
    }
    
    public TypeFunction getMethod(String methodName) {
        for (TypeFunction m : methods) {
            if (m.name.equals(methodName)) return m;
        }
        if (father != null) return father.getMethod(methodName);
        return null;
    }
}