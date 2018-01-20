package io.github.jonhshepard.tifromtext;


import io.github.jonhshepard.tifromtext.frames.JavaMakerApp;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JonhSHEPARD
 */
public class TiFromText {

	private static List<Arguments> arguments = new ArrayList<>();
	private static TiFromText INSTANCE;

	public static boolean isDebug() {
		return arguments.contains(Arguments.DEBUG);
	}

	public static boolean isMaximized() {
		return arguments.contains(Arguments.MAXIMIZED);
	}

	public static TiFromText getINSTANCE() {
		return INSTANCE;
	}

	public static void main(String[] args) {
		for (String s : args) {
			if (s.toLowerCase().equalsIgnoreCase("-maximized") ||
					s.toLowerCase().equalsIgnoreCase("maximized")) {
				arguments.add(Arguments.MAXIMIZED);
			} else if (s.toLowerCase().equalsIgnoreCase("-debug") ||
					s.toLowerCase().equalsIgnoreCase("debug")) {
				arguments.add(Arguments.DEBUG);
			}
		}
		new TiFromText();
	}

	private TiFromText() {
		INSTANCE = this;

		new Thread(() -> Application.launch(JavaMakerApp.class)).start();
	}
}
