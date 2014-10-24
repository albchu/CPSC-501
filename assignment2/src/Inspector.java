import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Vector;

public class Inspector
{
	public void inspect(Object obj, boolean recursive)
	{
		Vector objectsToInspect = new Vector();
		Class ObjClass = obj.getClass();

		System.out.println("inside inspector: " + obj + " (recursive = " + recursive + ")");

		System.out.println("Current Class: " + ObjClass.getName());
		System.out.println("Declaring Class: " + ObjClass.getDeclaringClass());
		System.out.println("Intermediate Super Class: " + ObjClass.getSuperclass());



		// inspect the current class
		inspectConstructors(ObjClass);
		inspectMethods(ObjClass);
		inspectInterfaces(ObjClass);
//		inspectFields(obj, ObjClass, objectsToInspect);

		if (recursive)
			inspectFieldClasses(obj, ObjClass, objectsToInspect, recursive);

	}
	
	private void inspectConstructors(Class<?> objClass)
	{
		for (Method method : objClass.getMethods())
		{
			System.out.println("Method Name: " + method.getName());
			System.out.println("\tModifier: " + Modifier.toString(method.getModifiers()));
			System.out.println("\tReturn Type: " + method.getReturnType());
			for (Class exception : method.getExceptionTypes())
				System.out.println("\tException thrown: " + exception.getName());
			for (Class<?> paramTypes : method.getParameterTypes())
				System.out.println("\tParameter types: " + paramTypes.getName());
		}
	}
	
	private void inspectMethods(Class<?> objClass)
	{
		for (Method method : objClass.getMethods())
		{
			System.out.println("Method Name: " + method.getName());
			System.out.println("\tModifier: " + Modifier.toString(method.getModifiers()));
			System.out.println("\tReturn Type: " + method.getReturnType());
			for (Class<?> exception : method.getExceptionTypes())
				System.out.println("\tException thrown: " + exception.getName());
			for (Class<?> paramTypes : method.getParameterTypes())
				System.out.println("\tParameter types: " + paramTypes.getName());
		}
	}

	private void inspectInterfaces(Class<?> objClass)
	{
		for (Class<?> clazz : objClass.getInterfaces())
			System.out.println("Implements Class: " + clazz);
	}

	// -----------------------------------------------------------
	private void inspectFieldClasses(Object obj, Class ObjClass, Vector objectsToInspect, boolean recursive)
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

				System.out.println("Field: " + f.getName() + " = " + f.get(obj));
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
