package types;

public abstract class Type
{
	public String name;

	public boolean isClass(){ return false;}
	public boolean isArray(){ return false;}
	public boolean isVoid() { return false; }
    public boolean isInt() { return this == TypeInt.getInstance(); }
    public boolean isString() { return this == TypeString.getInstance(); }

    /* Check if this type can be assigned to 'targetType' */
    public boolean isAssignableTo(Type targetType) {
        if (this == targetType) return true;
        // Nill is assignable to any class or array
        if (this == TypeVoid.getInstance() && (targetType.isClass() || targetType.isArray())) return true; // treating void as nil/bottom for assignment if needed, or check specific Nil type
        return false;
    }
}