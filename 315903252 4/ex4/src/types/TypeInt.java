package types;
public class TypeInt extends Type {
	private static TypeInt instance = null;
	protected TypeInt() { this.name = "int"; }
	public static TypeInt getInstance() {
		if (instance == null) instance = new TypeInt();
		return instance;
	}
}