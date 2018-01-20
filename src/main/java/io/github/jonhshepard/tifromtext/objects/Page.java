package io.github.jonhshepard.tifromtext.objects;

import io.github.jonhshepard.tifromtext.EnumObjType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JonhSHEPARD
 */
public class Page extends TreeObj {

	private List<PageLine> lines;

	public Page(String name, int position) {
		super(name, position, EnumObjType.PAGE);
		this.lines = new ArrayList<>();
	}

	public List<PageLine> getLines() {
		return lines;
	}

	public void setLines(List<PageLine> lines) {
		this.lines = lines;
	}
}
