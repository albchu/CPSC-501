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
		inspect(origInstance, ObjClass, recursive);	
	}
	
	/**
	 * The real inspect method that can recursively reflect through the original object instance
	 * @param origInstance
	 * @param ObjClass
	 * @param recursive
	 */
	public void inspect(Object origInstance, Class ObjClass, boolean recursive)
	{
		Vector<Field> objectsToInspect = new Vector();
		Class<?> superClass = ObjClass.getSuperclass();
		inspectedClasses.add(ObjClass);
		
		display("inside inspector: " + ObjClass + " (recursive = " + recursive + ")");
		
		display("Current Class: " + ObjClass.getName());
		display("Declaring Class: " + ObjClass.getDeclaringClass());
		display("Intermediate Super Class: " + ObjClass.getSuperclass());
		display();
		
		
		// inspect the current class
		inspectConstructors(ObjClass);
		inspectInterfaces(ObjClass);
		inspectFields(origInstance, ObjClass, objectsToInspect);
		inspectMethods(ObjClass);
		if (recursive)
			inspectFieldClasses(origInstance, ObjClass, objectsToInspect, recursive);
//		//inspect(obj.getClass().getSuperclass(), recursive);
//		display(inspectedClasses.contains(superClass));
		if(ObjClass.getSuperclass() != null && !inspectedClasses.contains(ObjClass.getSuperclass()))
		{
//			inspectedClasses.add(superClass);
			inspect(origInstance, superClass, recursive);
		}
	}
	
	private void inspectConstructors(Class<?> objClass)
	{
		for (Constructor<?> constructor : objClass.getDeclaredConstructors())
		{
			//constructor.setAccessible(true);
			display("Constructor Name: " + constructor.getName());
			display("\tModifier: " + Modifier.toString(constructor.getModifiers()));
			for (Class exception : constructor.getExceptionTypes())
				display("\tException thrown: " + exception.getName());
			for (Class<?> paramTypes : constructor.getParameterTypes())
				display("\tParameter types: " + paramTypes.getName());
			display("\tDeclaring Class: " + constructor.getDeclaringClass().getName());
		}
//		if (objClass.getSuperclass() != null)
//			inspectConstructors(objClass.getSuperclass());
	}
	
	private void inspectMethods(Class<?> objClass)
	{
		for (Method method : objClass.getDeclaredMethods())
		{
			method.setAccessible(true);
			display("Method Name: " + method.getName());
			display("\tModifier: " + Modifier.toString(method.getModifiers()));
			display("\tReturn Type: " + method.getReturnType());
			for (Class<?> exception : method.getExceptionTypes())
				display("\tException thrown: " + exception.getName());
			for (Class<?> paramTypes : method.getParameterTypes())
				display("\tParameter types: " + paramTypes.getName());
			display("\tDeclaring Class: " + method.getDeclaringClass().getName());
		}
//		if (objClass.getSuperclass() != null)
//			inspectMethods(objClass.getSuperclass());
	}

	private void inspectInterfaces(Class<?> objClass)
	{
		for (Class<?> clazz : objClass.getInterfaces())
		{
			display("Implements Class: " + clazz);
			//display("\tDeclaring Class: " + clazz.getDeclaringClass().getName());
		}
		
//		if (objClass.getSuperclass() != null)
//			inspectInterfaces(objClass.getSuperclass());
	}

	// -----------------------------------------------------------
	private void inspectFieldClasses(Object obj, Class ObjClass, Vector objectsToInspect, boolean recursive)
	{

		if (objectsToInspect.size() > 0)
			display("---- Inspecting Field Classes ----");

		Enumeration e = objectsToInspect.elements();
		while (e.hasMoreElements())
		{
			Field f = (Field) e.nextElement();
			f.setAccessible(true);
			display("Inspecting Field: " + f.getName());
				try
				{
					display("******************");
					if(f.get(obj) != null)
					inspect(f.get(obj), f.get(obj).getClass(), recursive);
					display("******************");
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
			
			display("Field Name: " + field.getName());
			field.setAccessible(true);
					
			try
			{

				display("\tValue: " + field.get(obj));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			display("\tModifier: " + Modifier.toString(field.getModifiers()));
			display("\tType: " + field.getType().getName());
			display("\tDeclaring Class: " + field.getDeclaringClass().getName());
		}

//		if (ObjClass.getSuperclass() != null)
//			inspectFields(obj, ObjClass.getSuperclass(), objectsToInspect);
	}

	public static void main(String[] args)
	{
		display("Helloworld");
	}
}
