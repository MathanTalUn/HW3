package types;
public class TypeVoid extends Type {
	private static TypeVoid instance = null;
	protected TypeVoid() { this.name = "void"; }
	public static TypeVoid getInstance() {
		if (instance == null) instance = new TypeVoid();
		return instance;
	}
    @Override public boolean isVoid() { return true; }
}