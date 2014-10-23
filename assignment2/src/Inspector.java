import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Vector;

public class Inspector
{
	public void inspect(Object obj, boolean recursive)
	{
		Vector objectsToInspect = new Vector();
		Class ObjClass = obj.getClass();

		System.out.println("inside inspector: " + obj + " (recursive = "
				+ recursive + ")");

		// inspect the current class
		inspectFields(obj, ObjClass, objectsToInspect);

		if (recursive)
			inspectFieldClasses(obj, ObjClass, objectsToInspect, recursive);

	}

	// -----------------------------------------------------------
	private void inspectFieldClasses(Object obj, Class ObjClass,
			Vector objectsToInspect, boolean recursive)
	{

		if (objectsToInspect.size() > 0)
			System.out.println("---- Inspecting Field Classes ----");

		Enumeration e = objectsToInspect.elements();
		while (e.hasMoreElements())
		{
			Field f = (Field) e.nextElement();
			System.out.println("Inspecting Field: " + f.getName());

			try
			{
				System.out.println("******************");
				inspect(f.get(obj), recursive);
				System.out.println("******************");
			} catch (Exception exp)
			{
				exp.printStackTrace();
			}
		}
	}

	private void inspectFields(Object obj, Class ObjClass, Vector objectsToInspect)

	{

		if (ObjClass.getDeclaredFields().length >= 1)
		{
			Field f = ObjClass.getDeclaredFields()[0];

			f.setAccessible(true);

			if (!f.getType().isPrimitive())
				objectsToInspect.addElement(f);

			try
			{

				System.out
						.println("Field: " + f.getName() + " = " + f.get(obj));
			} catch (Exception e)
			{
			}
		}

		if (ObjClass.getSuperclass() != null)
			inspectFields(obj, ObjClass.getSuperclass(), objectsToInspect);
	}

	public static void main(String[] args)
	{
		System.out.println("Helloworld");
	}
}
