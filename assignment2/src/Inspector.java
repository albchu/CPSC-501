import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Vector;

public class Inspector
{
	public void inspect(Object obj, boolean recursive)
	{
		Vector<Field> objectsToInspect = new Vector();
		Class<?> ObjClass = obj.getClass();

		System.out.println("inside inspector: " + obj + " (recursive = " + recursive + ")");

		System.out.println("Current Class: " + ObjClass.getName());
		System.out.println("Declaring Class: " + ObjClass.getDeclaringClass());
		System.out.println("Intermediate Super Class: " + ObjClass.getSuperclass());



		// inspect the current class
//		inspectConstructors(ObjClass);
//		inspectMethods(ObjClass);
//		inspectInterfaces(ObjClass);
		inspectFields(obj, ObjClass, objectsToInspect);

		if (recursive)
			inspectFieldClasses(obj, ObjClass, objectsToInspect, recursive);

	}
	
	private void inspectConstructors(Class<?> objClass)
	{
		for (Constructor<?> constructor : objClass.getDeclaredConstructors())
		{
			constructor.setAccessible(true);
			System.out.println("Constructor Name: " + constructor.getName());
			System.out.println("\tModifier: " + Modifier.toString(constructor.getModifiers()));
			for (Class exception : constructor.getExceptionTypes())
				System.out.println("\tException thrown: " + exception.getName());
			for (Class<?> paramTypes : constructor.getParameterTypes())
				System.out.println("\tParameter types: " + paramTypes.getName());
		}
	}
	
	private void inspectMethods(Class<?> objClass)
	{
		for (Method method : objClass.getDeclaredMethods())
		{
			method.setAccessible(true);
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

	private void inspectFields(Object obj, Class<?> ObjClass, Vector<Field> objectsToInspect)

	{

		for (Field field : ObjClass.getDeclaredFields())
		{
			field.setAccessible(true);

			if (!field.getType().isPrimitive())
				objectsToInspect.addElement(field);
			
			System.out.println("Field Name: " + field.getName());
					
			try
			{

				System.out.println("\tValue: " + field.get(obj));
			} catch (Exception e)
			{
			}
			System.out.println("\tModifier: " + Modifier.toString(field.getModifiers()));
			System.out.println("\tType: " + field.getType().getName());
		}

		if (ObjClass.getSuperclass() != null)
			inspectFields(obj, ObjClass.getSuperclass(), objectsToInspect);
	}

	public static void main(String[] args)
	{
		System.out.println("Helloworld");
	}
}
