package objectGenerator.templates;

import java.util.ArrayList;
import java.util.List;

public class CollectionObjects {
	private List<?> listObjs;
	
	public CollectionObjects(){
		listObjs = new ArrayList<?>();
	}
	
	public List<?> getListObjs() {
		return listObjs;
	}

	public void setListObjs(List<?> listObjs) {
		this.listObjs = listObjs;
	}
	
}
