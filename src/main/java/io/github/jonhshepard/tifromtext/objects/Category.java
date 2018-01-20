package io.github.jonhshepard.tifromtext.objects;

import io.github.jonhshepard.tifromtext.EnumObjType;

/**
 * @author JonhSHEPARD
 */
public class Category extends TreeObj {

	public Category(String name, int position) {
		super(name, position, EnumObjType.CATEGORY);
	}
}
