import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.*;
import java.util.Vector;

public class Inspector
{
	private List<Class<?>> inspectedClasses;
	
	public Inspector()
	{
		inspectedClasses = new ArrayList<Class<?>>();
	}
	
	public void inspect(Object obj, boolean recursive)
	{
		Vector<Field> objectsToInspect = new Vector();
		Class<?> ObjClass = obj.getClass();
		inspect(obj,ObjClass, recursive);
	}
	
	public void inspect(Object obj, Class ObjClass, boolean recursive)
	{
		Vector<Field> objectsToInspect = new Vector();
		Class<?> superClass = ObjClass.getSuperclass();
		inspectedClasses.add(ObjClass);
		
		System.out.println("inside inspector: " + ObjClass + " (recursive = " + recursive + ")");
		
		System.out.println("Current Class: " + ObjClass.getName());
		System.out.println("Declaring Class: " + ObjClass.getDeclaringClass());
		System.out.println("Intermediate Super Class: " + ObjClass.getSuperclass());
		System.out.println();
		
		
		// inspect the current class
		inspectConstructors(ObjClass);
		inspectInterfaces(ObjClass);
		inspectFields(obj, ObjClass, objectsToInspect);
		inspectMethods(ObjClass);
		if (recursive)
			inspectFieldClasses(obj, ObjClass, objectsToInspect, recursive);
//		//inspect(obj.getClass().getSuperclass(), recursive);
//		System.out.println(inspectedClasses.contains(superClass));
		if(ObjClass.getSuperclass() != null && !inspectedClasses.contains(ObjClass.getSuperclass()))
		{
//			inspectedClasses.add(superClass);
			inspect(obj, superClass, recursive);
		}
		
	}
	
	private void inspectConstructors(Class<?> objClass)
	{
		for (Constructor<?> constructor : objClass.getDeclaredConstructors())
		{
			//constructor.setAccessible(true);
			System.out.println("Constructor Name: " + constructor.getName());
			System.out.println("\tModifier: " + Modifier.toString(constructor.getModifiers()));
			for (Class exception : constructor.getExceptionTypes())
				System.out.println("\tException thrown: " + exception.getName());
			for (Class<?> paramTypes : constructor.getParameterTypes())
				System.out.println("\tParameter types: " + paramTypes.getName());
			System.out.println("\tDeclaring Class: " + constructor.getDeclaringClass().getName());
		}
//		if (objClass.getSuperclass() != null)
//			inspectConstructors(objClass.getSuperclass());
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
			System.out.println("\tDeclaring Class: " + method.getDeclaringClass().getName());
		}
//		if (objClass.getSuperclass() != null)
//			inspectMethods(objClass.getSuperclass());
	}

	private void inspectInterfaces(Class<?> objClass)
	{
		for (Class<?> clazz : objClass.getInterfaces())
		{
			System.out.println("Implements Class: " + clazz);
			//System.out.println("\tDeclaring Class: " + clazz.getDeclaringClass().getName());
		}
		
//		if (objClass.getSuperclass() != null)
//			inspectInterfaces(objClass.getSuperclass());
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
			f.setAccessible(true);
			System.out.println("Inspecting Field: " + f.getName());
				try
				{
					System.out.println("******************");
					if(f.get(obj) != null)
					inspect(f.get(obj), f.get(obj).getClass(), recursive);
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

			if (!field.getType().isPrimitive())
				objectsToInspect.addElement(field);
			
			System.out.println("Field Name: " + field.getName());
			field.setAccessible(true);
					
			try
			{

				System.out.println("\tValue: " + field.get(obj));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("\tModifier: " + Modifier.toString(field.getModifiers()));
			System.out.println("\tType: " + field.getType().getName());
			System.out.println("\tDeclaring Class: " + field.getDeclaringClass().getName());
		}

//		if (ObjClass.getSuperclass() != null)
//			inspectFields(obj, ObjClass.getSuperclass(), objectsToInspect);
	}

	public static void main(String[] args)
	{
		System.out.println("Helloworld");
	}
}
