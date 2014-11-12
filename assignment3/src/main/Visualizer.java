package main;

import java.lang.reflect.*;
import java.util.*;

import static utilities.TextDisplay.*;

public class Visualizer
{
	private List<Class<?>> inspectedClasses;
	public Visualizer()
	{
		inspectedClasses = new ArrayList<Class<?>>();
	}
	
	/**
	 * In order to prevent an error where the a class.getClass() == Class.class rather than the actual class, this must call our real inspect method
	 * @param origInstance
	 * @param recursive
	 */
	public void visualize(Object origInstance, boolean recursive)
	{
		Class<?> ObjClass = origInstance.getClass();
		int depth = 0;
		visualize(origInstance, ObjClass, recursive, depth );	
	}
	
	/**
	 * The real inspect method that can recursively reflect through the original object instance
	 * @param origInstance
	 * @param ObjClass
	 * @param recursive
	 */
	public void visualize(Object origInstance, Class<?> ObjClass, boolean recursive, int depth)
	{
		if(ObjClass.isArray())
			visualizeArray(origInstance, recursive, depth);
		else if (ObjClass.isPrimitive())
			visualizePrimitive(origInstance, depth);
		else
			visualizeObject(origInstance, ObjClass, recursive, depth);
	}
	
	/**
	 * Assumption: origInstance is an array
	 * @param obj
	 * @param recursive
	 */
	private void visualizeArray(Object origInstance, boolean recursive, int depth)
	{
		display("Array Object:", depth);
		display("Reference: " + origInstance, depth + 1);
		Class<?> componentType = origInstance.getClass().getComponentType();
		display("Component Type: " + componentType.getName(), depth + 1);
		display();
		int arrayLength = Array.getLength(origInstance);
		display("Array size: " + arrayLength, depth + 1);
		display();
		for (int i = 0; i < arrayLength ; i++)
		{
			display("Element Index: " + i, depth + 1);
			Object elementObj = Array.get(origInstance, i);
			if (elementObj != null)
				visualize(elementObj, componentType, recursive, depth + 1);
			else
				display("Element: Null", depth + 1);
			display();
		}
		display();
	}
	
	private void visualizeObject(Object origInstance, Class<?> ObjClass, boolean recursive, int depth)
	{
		List<Field> objectsToInspect = new ArrayList<Field>();
		Class<?> superClass = ObjClass.getSuperclass();
		inspectedClasses.add(ObjClass);
		display("Recursion: " + recursive, depth);
		display("Class Name: " + ObjClass.getName(), depth);
		display("Declaring Class: " + ObjClass.getDeclaringClass(), depth);
		display("Intermediate Super Class: " + superClass, depth);
		
		// inspect the current class
		visualizeConstructors(ObjClass, depth);
		visualizeInterfaces(ObjClass, depth);
		visualizeFields(origInstance, ObjClass, objectsToInspect, depth);
		visualizeMethods(ObjClass, depth);
		if (recursive)
		{
			visualizeFieldClasses(origInstance, ObjClass, objectsToInspect, recursive, depth);
			
			if(superClass != null && !inspectedClasses.contains(ObjClass.getSuperclass()))
			{
				display("SUPERCLASS OF " + ObjClass.getName(), depth);
				visualize(origInstance, superClass, recursive, depth + 1);
			}
		}
	}
	
	/**
	 * Assumption: obj is primitive
	 * If the object is a primitive type, recursively parsing will only result in volitile low level values of little usefulness
	 * Only the value is of importance at the primitive level
	 * This method will only print out the value field of the primitive
	 * @param origInstance
	 */
	private void visualizePrimitive(Object obj, int depth)
	{
		try
		{
			Field valueField = obj.getClass().getDeclaredField("value");
			valueField.setAccessible(true);
			display("Primitive Value: " + valueField.get(obj).toString(), depth);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void visualizeConstructors(Class<?> objClass, int depth)
	{
		if(objClass.getDeclaredConstructors().length > 0)
			display("Constructors:", depth);
		else return;
		for (Constructor<?> constructor : objClass.getDeclaredConstructors())
		{
//			constructor.setAccessible(true);
			display("Name: " + constructor.getName(), depth + 1);
			display("Modifier: " + Modifier.toString(constructor.getModifiers()), depth + 1);
			for (Class<?> exception : constructor.getExceptionTypes())
				display("Exception thrown: " + exception.getName(), depth + 1);
			for (Class<?> paramTypes : constructor.getParameterTypes())
				display("Parameter types: " + paramTypes.getName(), depth + 1);
			display("Declaring Class: " + constructor.getDeclaringClass().getName(), depth + 1);
			display();
		}
		display();
	}
	
	private void visualizeMethods(Class<?> objClass, int depth)
	{
		if(objClass.getDeclaredMethods().length > 0)
			display("Methods:", depth);
		else return;
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
	}

	private void visualizeInterfaces(Class<?> objClass, int depth)
	{
		if(objClass.getInterfaces().length > 0)
			display("Interfaces:", depth);
		else return;
		for (Class<?> clazz : objClass.getInterfaces())
		{
			display(clazz.getName(), depth + 1);
			display();
		}
		display();
	}

	private void visualizeFieldClasses(Object obj, Class<?> ObjClass, List<Field> objectsToInspect, boolean recursive, int depth)
	{

		if (objectsToInspect.size() > 0)
			display("FIELD CLASSES:", depth);
		else return;
		for (Field field : objectsToInspect)
		{
			field.setAccessible(true);
			display("Field Class Name: " + field.getName(), depth + 1);
				try
				{
					Object fieldObj = field.get(obj);
					if(fieldObj == null)
						display("No value given", depth + 1);
					else
					{
						Visualizer newInspect = new Visualizer(); // Needs a new instance otherwise we may cut a superclass unexpectedly
						newInspect.visualize(fieldObj, fieldObj.getClass(), recursive, depth + 1); 
					}
				} catch (Exception exp)
				{
					exp.printStackTrace();
				}
				display();
		}
		display();
	}

	private void visualizeFields(Object obj, Class<?> ObjClass, List<Field> objectsToInspect, int depth)
	{
		if (ObjClass.getDeclaredFields().length > 0)
			display("Fields:", depth);
		else return;
		for (Field field : ObjClass.getDeclaredFields())
		{

			if (!field.getType().isPrimitive())
				objectsToInspect.add(field);
			
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
	}
}
