package io.github.jonhshepard.tifromtext.objects;

/**
 * @author JonhSHEPARD
 */
public class PageLine {

	private String content;
	private boolean breakLine;

	public PageLine(String content) {
		this.content = content;
		this.breakLine = false;
	}

	public PageLine(String content, boolean breakLine) {
		this.content = content;
		this.breakLine = breakLine;
	}

	public String getContent() {
		return content;
	}

	public boolean isBreakLine() {
		return breakLine;
	}
}
