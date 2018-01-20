package io.github.jonhshepard.tifromtext.objects;

import io.github.jonhshepard.tifromtext.EnumObjType;

/**
 * @author JonhSHEPARD
 */
public class TreeObj {

	private String name;
	private int position;
	private EnumObjType type;

	TreeObj(String name, int position, EnumObjType type) {
		this.name = name;
		this.position = position;
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public EnumObjType getType() {
		return this.type;
	}
}
