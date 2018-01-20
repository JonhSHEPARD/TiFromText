package io.github.jonhshepard.tifromtext.objects;

import io.github.jonhshepard.tifromtext.EnumObjType;

import java.util.List;

/**
 * @author JonhSHEPARD
 */
public class CategoryT extends TreeObj {

	private List<String> childs;

	public CategoryT(String name, int position, List<String> childs) {
		super(name, position, EnumObjType.CATEGORY);
		this.childs = childs;
	}

	public List<String> getChilds() {
		return childs;
	}

}
