import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static utilities.TextDisplay.*;

public class Inspector
{
	private List<Class<?>> inspectedClasses;
	public Inspector()
	{
		inspectedClasses = new ArrayList<Class<?>>();
	}
	
	/**
	 * In order to prevent an error where the a class.getClass() == Class.class rather than the actual class, this must call our real inspect method
	 * @param origInstance
	 * @param recursive
	 */
	public void inspect(Object origInstance, boolean recursive)
	{
		Class<?> ObjClass = origInstance.getClass();
		int depth = 0;
		inspect(origInstance, ObjClass, recursive, depth );	
	}
	
	/**
	 * The real inspect method that can recursively reflect through the original object instance
	 * @param origInstance
	 * @param ObjClass
	 * @param recursive
	 */
	public void inspect(Object origInstance, Class ObjClass, boolean recursive, int depth)
	{
		Vector<Field> objectsToInspect = new Vector();
		Class<?> superClass = ObjClass.getSuperclass();
		inspectedClasses.add(ObjClass);
		
		display("INSPECTING: ", depth);
		
		display("Current Class: " + ObjClass.getName(), depth);
		display("Recursion: " + recursive, depth);
		display("Declaring Class: " + ObjClass.getDeclaringClass(), depth);
		display("Intermediate Super Class: " + ObjClass.getSuperclass(), depth);
		display();
		
		
		// inspect the current class
		inspectConstructors(ObjClass, depth);
		inspectInterfaces(ObjClass, depth);
		inspectFields(origInstance, ObjClass, objectsToInspect, depth);
		inspectMethods(ObjClass, depth);
		if (recursive)
		{
			inspectFieldClasses(origInstance, ObjClass, objectsToInspect, recursive, depth);
			
			if(ObjClass.getSuperclass() != null && !inspectedClasses.contains(ObjClass.getSuperclass()))
				inspect(origInstance, superClass, recursive, depth + 1);
		}
		
//		//inspect(obj.getClass().getSuperclass(), recursive);
//		display(inspectedClasses.contains(superClass));
	}
	
	private void inspectConstructors(Class<?> objClass, int depth)
	{
		if(objClass.getDeclaredConstructors().length > 0)
			display("Constructors:", depth);
		
		for (Constructor<?> constructor : objClass.getDeclaredConstructors())
		{
			//constructor.setAccessible(true);
			display("Name: " + constructor.getName(), depth + 1);
			display("Modifier: " + Modifier.toString(constructor.getModifiers()), depth + 1);
			for (Class exception : constructor.getExceptionTypes())
				display("Exception thrown: " + exception.getName(), depth + 1);
			for (Class<?> paramTypes : constructor.getParameterTypes())
				display("Parameter types: " + paramTypes.getName(), depth + 1);
			display("Declaring Class: " + constructor.getDeclaringClass().getName(), depth + 1);
			display();
		}
		display();
		
//		if (objClass.getSuperclass() != null)
//			inspectConstructors(objClass.getSuperclass());
	}
	
	private void inspectMethods(Class<?> objClass, int depth)
	{
		if(objClass.getDeclaredMethods().length > 0)
			display("Methods:", depth);
		
		for (Method method : objClass.getDeclaredMethods())
		{
			method.setAccessible(true);
			display("Name: " + method.getName(), depth + 1);
			display("Modifier: " + Modifier.toString(method.getModifiers()), depth + 1);
			display("Return Type: " + method.getReturnType(), depth + 1);
			for (Class<?> exception : method.getExceptionTypes())
				display("Exception thrown: " + exception.getName(), depth + 1);
			for (Class<?> paramTypes : method.getParameterTypes())
				display("Parameter types: " + paramTypes.getName(), depth + 1);
			display("Declaring Class: " + method.getDeclaringClass().getName(), depth + 1);
			display();
		}
		display();
//		if (objClass.getSuperclass() != null)
//			inspectMethods(objClass.getSuperclass());
	}

	private void inspectInterfaces(Class<?> objClass, int depth)
	{
		if(objClass.getInterfaces().length > 0)
			display("Interfaces:", depth);
		for (Class<?> clazz : objClass.getInterfaces())
		{
			display(clazz.getName(), depth + 1);
			display();
		}
		display();
		
//		if (objClass.getSuperclass() != null)
//			inspectInterfaces(objClass.getSuperclass());
	}

	// -----------------------------------------------------------
	private void inspectFieldClasses(Object obj, Class ObjClass, Vector objectsToInspect, boolean recursive, int depth)
	{

		if (objectsToInspect.size() > 0)
			display("---- Inspecting Field Classes ----", depth);

		Enumeration e = objectsToInspect.elements();
		while (e.hasMoreElements())
		{
			Field f = (Field) e.nextElement();
			f.setAccessible(true);
			display("Inspecting Field: " + f.getName(), depth);
				try
				{
					display("******************", depth);
					if(f.get(obj) != null)
					inspect(f.get(obj), f.get(obj).getClass(), recursive, depth);
					display("******************", depth);
				} catch (Exception exp)
				{
					exp.printStackTrace();
				}
		}
	}

	private void inspectFields(Object obj, Class<?> ObjClass, Vector<Field> objectsToInspect, int depth)
	{
		if (ObjClass.getDeclaredFields().length > 0)
			display("Fields:", depth);
		
		for (Field field : ObjClass.getDeclaredFields())
		{

			if (!field.getType().isPrimitive())
				objectsToInspect.addElement(field);
			
			display("Name: " + field.getName(), depth + 1);
			field.setAccessible(true);
					
			try
			{

				display("Value: " + field.get(obj), depth + 1);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			display("Modifier: " + Modifier.toString(field.getModifiers()), depth + 1);
			display("Type: " + field.getType().getName(), depth + 1);
			display("Declaring Class: " + field.getDeclaringClass().getName(), depth + 1);
			display();
		}
		display();

//		if (ObjClass.getSuperclass() != null)
//			inspectFields(obj, ObjClass.getSuperclass(), objectsToInspect);
	}

	public static void main(String[] args)
	{
		display("Helloworld", 0);
	}
}
