package main;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

// Following contains blocks of code adapted from Java Reflection: In Action
public class Serializer {
	
	public List<Document> serializeList(List<Object> objs) {
		List<Document> docs = new ArrayList<Document>();
		for(Object obj: objs)
			docs.add(serialize(obj));
		return docs;
	}
	
	public void toFile(List<Object> objs)
	{
		int i = 0;
		for(Document doc : serializeList(objs))
		{
			XMLOutputter xmlOutput = new XMLOutputter();
			 
			// display nice nice
			xmlOutput.setFormat(Format.getPrettyFormat());
			try {
				xmlOutput.output(doc, new FileWriter("Serialized" + i + ".xml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Document serialize(Object obj) {
		return serialize(obj, new Document(new Element("serialized")), new HashMap<Object, String>());
	}

	public static Document serialize(Object obj, Document target,
			Map<Object, String> map) {

		// Create unique id for obj to be serialized
		String id = Integer.toString(map.size());
		map.put(obj, id);
		Class<?> objclass = obj.getClass();

		// Creates an xml element for object
		Element objElem = new Element("object");
		objElem.setAttribute("class", objclass.getName());
		objElem.setAttribute("id", id);
		target.getRootElement().addContent(objElem);

		// Handles arrays differently from scalars
		if (objclass.isArray()) 
		{
			Class<?> componentType = objclass.getComponentType();

			int length = Array.getLength(obj);
			objElem.setAttribute("length", Integer.toString(length));
			for (int i = 0; i < length; i++) {
				objElem.addContent(serializeVariable(componentType,
						Array.get(obj, i), target, map));
			}
		} 
		else 
		{
			// List<Field> fields = getNonStaticFields(objclass);
			for (Field field : getNonStaticFields(objclass)) {
				field.setAccessible(true);
				Element fieldElem = new Element("field");
				fieldElem.setAttribute("name", field.getName());
				Class<?> declaringClass = field.getDeclaringClass();
				fieldElem.setAttribute("declaringclass", declaringClass.getName());

				Class<?> fieldtype = field.getType();
				Object child = null;
				try {
					child = field.get(obj);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

				fieldElem.addContent(serializeVariable(fieldtype, child, target,
						map));

				objElem.addContent(fieldElem);
			}		
		}

		return target;
	}

	private static Element serializeVariable(Class<?> fieldtype, Object child,
			Document target, Map<Object, String> map) {
		if (child == null) {
			return new Element("null");
		} else if (!fieldtype.isPrimitive()) {
			Element reference = new Element("reference");
			if (map.containsKey(child)) {
				reference.setText(map.get(child).toString());
			} else {
				reference.setText(Integer.toString(map.size()));
				serialize(child, target, map);
			}
			return reference;
		} else {
			Element value = new Element("value");
			value.setText(child.toString());
			return value;
		}
	}

	/**
	 * Returns a list of all fields from the class
	 * 
	 * @param objclass
	 * @return
	 */
	private static List<Field> getNonStaticFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : clazz.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers()))
				fields.add(field);
		}
		if (clazz.getSuperclass() != null)
			fields.addAll(getNonStaticFields(clazz.getSuperclass()));
		return fields;
	}
}
