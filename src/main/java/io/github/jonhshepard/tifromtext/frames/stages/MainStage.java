package io.github.jonhshepard.tifromtext.frames.stages;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.github.jonhshepard.tifromtext.EnumObjType;
import io.github.jonhshepard.tifromtext.TiConverter;
import io.github.jonhshepard.tifromtext.TiFromText;
import io.github.jonhshepard.tifromtext.frames.stages.main.CenterPanel;
import io.github.jonhshepard.tifromtext.frames.stages.main.LeftPanel;
import io.github.jonhshepard.tifromtext.objects.Category;
import io.github.jonhshepard.tifromtext.objects.CategoryT;
import io.github.jonhshepard.tifromtext.objects.Page;
import io.github.jonhshepard.tifromtext.objects.TreeObj;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author JonhSHEPARD
 */
public class MainStage extends Stage {

	public MainStage() {
		this.setTitle("TiFromText");
		this.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));

		this.setResizable(true);
		this.setMinHeight(460);
		this.setMinWidth(920);

		if(TiFromText.isMaximized()) this.setMaximized(true);

		GridPane main = new GridPane();

		/*
		 * Row constraints
		 */
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(8);
		main.getRowConstraints().add(row1);
		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(92);
		main.getRowConstraints().add(row2);

		/*
		 * Column constraints
		 */
		ColumnConstraints leftCol = new ColumnConstraints();
		leftCol.setPercentWidth(30);
		main.getColumnConstraints().add(leftCol);
		ColumnConstraints rightCol = new ColumnConstraints();
		rightCol.setPercentWidth(70);
		main.getColumnConstraints().add(rightCol);

		/*
		 * Panes
		 */
		LeftPanel panel = new LeftPanel() {
			@Override
			public void selectionChanges(TreeItem<TreeObj> selected) {
				if (selected == null || selected.getValue() instanceof Category) {
					removeCenterPanel(main);
				} else {
					main.add(new CenterPanel((Page) selected.getValue()) {
						@Override
						public void save(Page p) {
							selected.setValue(p);
							getTreeView().getSelectionModel().clearSelection();
							removeCenterPanel(main);
						}
					}, 1, 1);
				}
				super.selectionChanges(selected);
			}
		};
		main.add(panel, 0, 1);

		HBox toolbar = new HBox();
		toolbar.setAlignment(Pos.CENTER_LEFT);
		toolbar.setPadding(new Insets(0, 5, 0, 5));
		toolbar.setSpacing(10);
		HBox.setHgrow(toolbar, Priority.ALWAYS);

		Button loadFromJson = new Button("Ouvrir");
		loadFromJson.getStyleClass().setAll("btn", "btn-sm", "btn-primary");
		loadFromJson.setOnMouseClicked(event -> {

			Alert message = new Alert(Alert.AlertType.NONE, "Code du programme",
					new ButtonType("Entrer manuellement", ButtonBar.ButtonData.BACK_PREVIOUS),
					new ButtonType("Ouvrir un fichier", ButtonBar.ButtonData.NEXT_FORWARD),
					ButtonType.CANCEL);
			Optional<ButtonType> type = message.showAndWait();

			if(type.isPresent()) {
				if(type.get().getButtonData().equals(ButtonBar.ButtonData.BACK_PREVIOUS)) {
					TextInputDialog textInputDialog = new TextInputDialog("");
					textInputDialog.setTitle("Input");
					textInputDialog.setHeaderText("Entrez le json: ");
					Optional<String> val = textInputDialog.showAndWait();

					if (val.isPresent()) {
						try {
							CategoryT root = new Gson().fromJson(val.get(), CategoryT.class);

							loadCategory(panel.getTreeView(), root, null);
						} catch (JsonSyntaxException e) {
							new Alert(Alert.AlertType.ERROR, "Erreur lors de l'analyse du json !\n\nMessage: " + e.getMessage()).show();
						}
					}
				} else if (type.get().getButtonData().equals(ButtonBar.ButtonData.NEXT_FORWARD)) {
					FileChooser file = new FileChooser();
					file.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Fichier TI Java", "*.ti_java"));
					file.setTitle("Choix du fichier");
					File f = file.showOpenDialog(this);

					if (f != null && f.exists()) {
						try {
							String s = String.join("", Files.readAllLines(f.toPath()));
							CategoryT root = new Gson().fromJson(s, CategoryT.class);

							loadCategory(panel.getTreeView(), root, null);
						} catch (IOException e) {
							e.printStackTrace();
							new Alert(Alert.AlertType.ERROR, "Une erreur est survenue lors de l'ouverture !\n\nMessage: " + e.getMessage());
						} catch (JsonSyntaxException e) {
							new Alert(Alert.AlertType.ERROR, "Erreur lors de l'analyse du fichier !\n\nMessage: " + e.getMessage()).show();
						}
					}
				}
			}
		});
		toolbar.getChildren().add(loadFromJson);

		Button saveToJson = new Button("Enregistrer");
		saveToJson.getStyleClass().setAll("btn", "btn-sm", "btn-primary");
		saveToJson.setOnMouseClicked(event -> {
			if (panel.getTreeView().getRoot().getChildren().size() > 0) {
				String s = saveCategory(panel.getTreeView().getRoot());

				StringSelection selection = new StringSelection(s);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);

				Alert message = new Alert(Alert.AlertType.INFORMATION, "Code du programme copié dans le presse-papier (Ctrl+V pour le coller)", new ButtonType("Créer un fichier"), ButtonType.FINISH);
				Optional<ButtonType> type = message.showAndWait();

				if (type.isPresent() && type.get().getButtonData().equals(ButtonBar.ButtonData.OTHER)) {

					FileChooser file = new FileChooser();
					file.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Fichier TI Java", "*.ti_java"));
					file.setInitialFileName("prog.ti_java");
					file.setTitle("Choix du fichier d'enregistrement");
					File f = file.showSaveDialog(this);

					if (f != null && s != null) {
						try {
							if (Files.write(f.toPath(), Collections.singletonList(s), StandardCharsets.UTF_8).toFile().exists()) {
								new Alert(Alert.AlertType.CONFIRMATION, "Fichier enregisté !");
							}
						} catch (IOException e) {
							e.printStackTrace();
							new Alert(Alert.AlertType.ERROR, "Une erreur est survenue lors de l'écriture !\n\nMessage: " + e.getMessage());
						}
					}
				}
			} else {
				new Alert(Alert.AlertType.ERROR, "Programme vide !").show();
			}
		});
		toolbar.getChildren().add(saveToJson);

		Button convert = new Button("Convertir");
		convert.getStyleClass().setAll("btn", "btn-sm", "btn-primary");
		convert.setOnMouseClicked(event -> {
			if (panel.getTreeView().getRoot().getChildren().size() > 0) {
				new TiConverter(this, panel.getTreeView().getRoot());
			} else {
				new Alert(Alert.AlertType.ERROR, "Programme vide !").show();
			}
		});
		toolbar.getChildren().add(convert);

		main.add(toolbar, 0, 0, 2, 1);

		this.setScene(new Scene(main, 920, 470));
		//this.getScene().getStylesheets().add(getClass().getResource("/css/custom.css").toExternalForm());
	}

	private String saveCategory(TreeItem<TreeObj> category) {
		if (category.getValue() instanceof Category) {
			List<String> obj = new ArrayList<>();

			for (TreeItem<TreeObj> child : category.getChildren()) {
				if (child.getValue() instanceof Category) {
					obj.add(saveCategory(child));
				} else {
					obj.add(new Gson().toJson(child.getValue()));
				}
			}

			return new Gson().toJson(new CategoryT(category.getValue().getName(),
					category.getValue().getPosition(),
					obj));
		}
		return null;
	}

	private void loadCategory(TreeView<TreeObj> treeView, CategoryT category, TreeItem<TreeObj> parent) {
		Category validCat = new Category(category.getName(), category.getPosition());

		TreeItem<TreeObj> menu = new TreeItem<>(validCat, new ImageView(new Image(this.getClass().getResourceAsStream("/images/menu.png"))));
		menu.setExpanded(true);

		if (parent == null) {
			treeView.setRoot(menu);
		} else {
			parent.getChildren().add(menu);
		}

		for (String objS : category.getChilds()) {
			TreeObj obj = new Gson().fromJson(objS, TreeObj.class);

			if (obj.getType() == EnumObjType.CATEGORY) {
				CategoryT cat = new Gson().fromJson(objS, CategoryT.class);
				loadCategory(treeView, cat, menu);
			} else {
				Page page = new Gson().fromJson(objS, Page.class);
				menu.getChildren().add(new TreeItem<>(page, new ImageView(new Image(this.getClass().getResourceAsStream("/images/page.png")))));
			}
		}
	}

	private void removeCenterPanel(GridPane main) {
		List<Node> toRemove = main.getChildren().stream().filter(n -> n instanceof CenterPanel).collect(Collectors.toList());
		main.getChildren().removeAll(toRemove);
	}

}
