package io.github.jonhshepard.tifromtext;

import io.github.jonhshepard.tifromtext.frames.stages.MainStage;
import io.github.jonhshepard.tifromtext.objects.Category;
import io.github.jonhshepard.tifromtext.objects.Page;
import io.github.jonhshepard.tifromtext.objects.PageLine;
import io.github.jonhshepard.tifromtext.objects.TreeObj;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author JonhSHEPARD
 */
public class TiConverter {

	private List<String> variables = new ArrayList<>();
	private HashMap<TreeObj, String> vars = new HashMap<>();

	public TiConverter(MainStage stage, TreeItem<TreeObj> parent) {
		for (int i = 0; i <= 9; i++) {
			for (int j = 0; j < 26; j++) {
				variables.add(intToLetter(j) + i);
				variables.add(i + intToLetter(j));
			}
			variables.add(i + "");
		}
		variables.remove("A0");

		if (parent.getValue() instanceof Category) {
			giveVar(parent);

			List<String> s = makeMenu(parent, "");

			StringSelection selection = new StringSelection(String.join("\n", s));
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);

			Alert message = new Alert(Alert.AlertType.INFORMATION, "Programme copié dans le presse-papier (Ctrl+V pour le coller)", new ButtonType("Créer un fichier texte"), ButtonType.FINISH);
			Optional<ButtonType> type = message.showAndWait();

			if (type.isPresent() && type.get().getButtonData().equals(ButtonBar.ButtonData.OTHER)) {
				FileChooser file = new FileChooser();
				file.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Fichier texte", "*.txt"));
				file.setInitialFileName("prog.txt");
				file.setTitle("Choix du fichier d'enregistrement");
				File f = file.showSaveDialog(stage);

				if (f != null) {
					try {
						if(Files.write(f.toPath(), s, StandardCharsets.UTF_8).toFile().exists()) {
							new Alert(Alert.AlertType.CONFIRMATION, "Fichier enregisté !");
						}
					} catch (IOException e) {
						e.printStackTrace();
						new Alert(Alert.AlertType.ERROR, "Une erreur est survenue lors de l'écriture !\n\nMessage: " + e.getMessage());
					}
				}
			}
		}
	}

	private void giveVar(TreeItem<TreeObj> cat) {
		vars.put(cat.getValue(), getVariable());
		for (TreeItem<TreeObj> obj : cat.getChildren()) {
			if (obj.getValue() instanceof Category) {
				giveVar(obj);
			} else {
				vars.put(obj.getValue(), getVariable());
			}
		}
	}

	private List<String> makeMenu(TreeItem<TreeObj> menu, String parent) {
		List<String> list = new ArrayList<>();
		String currentLbl = vars.get(menu.getValue());
		list.add("Lbl " + currentLbl);
		list.add("EffÉcran");
		String MENU = "Menu(\"" + menu.getValue().getName() + "\",";
		for (TreeItem<TreeObj> obj : menu.getChildren()) {
			MENU += "\"" + obj.getValue().getName() + "\"," + vars.get(obj.getValue()) + ",";
		}
		MENU += ((menu.getValue().getPosition() == -1) ? "\"Quitter\",A0" : "\"Retour\"," + parent) + ")";
		list.add(MENU);

		for (TreeItem<TreeObj> obj : menu.getChildren()) {
			if (obj.getValue() instanceof Category) {
				list.addAll(makeMenu(obj, currentLbl));
			} else {
				list.addAll(makePage(obj, currentLbl));
			}
		}

		if (menu.getValue().getPosition() == -1) list.add("Lbl A0");
		return list;
	}

	private List<String> makePage(TreeItem<TreeObj> page, String parent) {
		List<String> list = new ArrayList<>();

		if (!(page.getValue() instanceof Page)) return list;

		list.add("Lbl " + vars.get(page.getValue()));
		list.add("EffÉcran");

		for (PageLine p : ((Page) page.getValue()).getLines()) {
			String prefix = (p.isBreakLine()) ? "Pause" : "Disp";
			String content = (p.getContent().equalsIgnoreCase("/Break/")) ? "" : p.getContent();
			list.add(prefix + " \"" + content + "\"");
		}

		list.add("Goto " + parent);

		return list;
	}

	private String getVariable() {
		String var = variables.get(0);
		variables.remove(0);
		return var;
	}

	private String intToLetter(int i) {
		switch (i) {
			case 0:
				return "A";
			case 1:
				return "B";
			case 2:
				return "C";
			case 3:
				return "D";
			case 4:
				return "E";
			case 5:
				return "F";
			case 6:
				return "G";
			case 7:
				return "H";
			case 8:
				return "I";
			case 9:
				return "J";
			case 10:
				return "K";
			case 11:
				return "L";
			case 12:
				return "M";
			case 13:
				return "N";
			case 14:
				return "O";
			case 15:
				return "P";
			case 16:
				return "Q";
			case 17:
				return "R";
			case 18:
				return "S";
			case 19:
				return "T";
			case 20:
				return "U";
			case 21:
				return "V";
			case 22:
				return "W";
			case 23:
				return "X";
			case 24:
				return "Y";
			case 25:
				return "Z";
		}
		return "";
	}

}
