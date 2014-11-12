package main;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

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
				xmlOutput.output(doc, new FileWriter(++i + "file.xml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Document serialize(Object obj) {
		return serializeHelper(obj, new Document(new Element("serialized")),
				new IdentityHashMap());
	}

	private static Document serializeHelper(Object obj, Document target,
			Map table) {

		// Document target = new Document(new Element("serialized"));
		// Map table = new IdentityHashMap<>();

		// Create unique id for obj to be serialized
		String id = Integer.toString(table.size());
		table.put(obj, id);
		Class objclass = obj.getClass();

		// Creates an xml element for object
		Element oElt = new Element("object");
		oElt.setAttribute("class", objclass.getName());
		oElt.setAttribute("id", id);
		target.getRootElement().addContent(oElt);

		// Handles arrays differently from scalars
		if (!objclass.isArray()) {
			// List<Field> fields = getNonStaticFields(objclass);
			for (Field field : getNonStaticFields(objclass)) {
				if (!Modifier.isPublic(field.getModifiers()))
					field.setAccessible(true);
				Element fElt = new Element("field");
				fElt.setAttribute("name", field.getName());
				Class declClass = field.getDeclaringClass();
				fElt.setAttribute("declaringclass", declClass.getName());

				Class fieldtype = field.getType();
				Object child = null;
				try {
					child = field.get(obj);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (Modifier.isTransient(field.getModifiers())) {
					child = null;
				}
				fElt.addContent(serializeVariable(fieldtype, child, target,
						table));

				oElt.addContent(fElt);
			}
		} else {
			Class componentType = objclass.getComponentType();

			int length = Array.getLength(obj);
			oElt.setAttribute("length", Integer.toString(length));
			for (int i = 0; i < length; i++) {
				oElt.addContent(serializeVariable(componentType,
						Array.get(obj, i), target, table));
			}
		}

		return target;
	}

	private static Element serializeVariable(Class fieldtype, Object child,
			Document target, Map table) {
		if (child == null) {
			return new Element("null");
		} else if (!fieldtype.isPrimitive()) {
			Element reference = new Element("reference");
			if (table.containsKey(child)) {
				reference.setText(table.get(child).toString());
			} else {
				reference.setText(Integer.toString(table.size()));
				serializeHelper(child, target, table);
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
