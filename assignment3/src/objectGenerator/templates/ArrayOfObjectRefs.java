package objectGenerator.templates;

public class ArrayOfObjectRefs {

	public Object[] arrayObjectRefs;

	public ArrayOfObjectRefs()
	{
		arrayObjectRefs = new Object[1000];
	}
	
	public Object[] getArrayObjectRefs() {
		return arrayObjectRefs;
	}

	public void setArrayObjectRefs(Object[] arrayObjectRefs) {
		this.arrayObjectRefs = arrayObjectRefs;
	}
	
	public void setArrayValue(Object value, int index) {
		this.arrayObjectRefs[index] = value;
	}
}
