package io.github.jonhshepard.tifromtext.frames.stages.main;

import io.github.jonhshepard.tifromtext.objects.Category;
import io.github.jonhshepard.tifromtext.objects.Page;
import io.github.jonhshepard.tifromtext.objects.TreeObj;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author JonhSHEPARD
 */
public abstract class LeftPanel extends BorderPane {

	private TreeView<TreeObj> treeView;
	private TreeItem<TreeObj> lastSelected = null;

	private Button addMenu;
	private Button addPage;
	private Button delete;

	protected LeftPanel() {
		super();

		Category rootCat = new Category("Catégories", -1);
		TreeItem<TreeObj> rootNode = new TreeItem<>(rootCat, new ImageView(new Image(this.getClass().getResourceAsStream("/images/menu.png"))));
		rootNode.setExpanded(true);
		treeView = new TreeView<>(rootNode);
		treeView.setCellFactory(p -> new LeftPanelTreeCell());
		treeView.prefWidthProperty().bind(this.prefWidthProperty());
		//this.(treeView, 0, 0);
		this.setCenter(treeView);

		HBox toolbar = new HBox();
		toolbar.setAlignment(Pos.CENTER_LEFT);
		toolbar.setPadding(new Insets(0, 5, 0, 5));
		toolbar.setSpacing(10);
		toolbar.setMinHeight(40);
		HBox.setHgrow(toolbar, Priority.ALWAYS);

		addMenu = new Button("+ Menu");
		addMenu.getStyleClass().setAll("btn", "btn-sm", "btn-success", "disabled");
		addMenu.setOnMouseClicked(event -> {
			if (!treeView.getSelectionModel().isEmpty()) {
				TreeItem<TreeObj> i = treeView.getSelectionModel().getSelectedItem();
				TreeObj obj = treeView.getSelectionModel().getSelectedItem().getValue();

				if (obj instanceof Category) {
					System.out.println(obj.getName() + " - " + i.getChildren().size());
					if (i.getChildren().size() < 7) {
						TextInputDialog textInputDialog = new TextInputDialog("");
						textInputDialog.setTitle("Titre de menu");
						textInputDialog.setHeaderText("Entrez le titre du menu :");
						Optional<String> val = textInputDialog.showAndWait();

						if (val.isPresent()) {
							String s = val.get();

							if (s.length() < 20 && s.length() > 2) {
								Category c = new Category(s, i.getChildren().size());
								TreeItem<TreeObj> item = new TreeItem<>(c, new ImageView(new Image(this.getClass().getResourceAsStream("/images/menu.png"))));
								item.setExpanded(true);
								treeView.getSelectionModel().getSelectedItem().getChildren().add(item);
							}
						}
					}
				}
			}
		});
		addMenu.setDisable(true);
		toolbar.getChildren().add(addMenu);


		addPage = new Button("+ Page");
		addPage.getStyleClass().setAll("btn", "btn-sm", "btn-success", "disabled");
		addPage.setTextAlignment(TextAlignment.CENTER);
		addPage.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!treeView.getSelectionModel().isEmpty()) {
					TreeItem<TreeObj> i = treeView.getSelectionModel().getSelectedItem();
					TreeObj obj = treeView.getSelectionModel().getSelectedItem().getValue();

					if (obj instanceof Category) {
						if (i.getChildren().size() < 7) {
							TextInputDialog textInputDialog = new TextInputDialog("");
							textInputDialog.setTitle("Titre de page");
							textInputDialog.setHeaderText("Entrez le titre de la page :");
							Optional<String> val = textInputDialog.showAndWait();

							if (val.isPresent()) {
								String s = val.get();

								if (s.length() < 20 && s.length() > 2) {
									Page c = new Page(s, i.getChildren().size());
									TreeItem<TreeObj> item = new TreeItem<>(c, new ImageView(new Image(this.getClass().getResourceAsStream("/images/page.png"))));
									item.setExpanded(true);
									treeView.getSelectionModel().getSelectedItem().getChildren().add(item);
								}
							}
						}
					}
				}
			}
		});
		addPage.setDisable(true);
		toolbar.getChildren().add(addPage);


		delete = new Button("-");
		delete.getStyleClass().setAll("btn", "btn-sm", "btn-danger", "disabled");
		delete.setOnMouseClicked(event -> {
			if (!treeView.getSelectionModel().isEmpty()) {
				TreeObj obj = treeView.getSelectionModel().getSelectedItem().getValue();

				if (obj.getPosition() >= 0) {
					TreeItem<TreeObj> parent1 = treeView.getSelectionModel().getSelectedItem().getParent();

					if (parent1.getValue() instanceof Category) {
						List<TreeItem<TreeObj>> toRemove = parent1.getChildren().stream().filter(o -> o.getValue().equals(obj)).collect(Collectors.toList());

						if (toRemove.size() > 0) {
							parent1.getChildren().removeAll(toRemove);
							for (int i = 0; i < parent1.getChildren().size(); i++) {
								TreeObj child = parent1.getChildren().get(i).getValue();
								child.setPosition(i);
							}

							if (treeView.getSelectionModel().isEmpty() || !treeView.getSelectionModel().getSelectedItem().equals(lastSelected)) {
								lastSelected = (treeView.getSelectionModel().isEmpty()) ? null : treeView.getSelectionModel().getSelectedItem();
								selectionChanges(lastSelected);
							}
						}
					}
				}
			}
		});
		delete.setDisable(true);
		toolbar.getChildren().add(delete);

		treeView.setOnMouseClicked(event -> {
			if (!treeView.getSelectionModel().isEmpty()) {
				if (!treeView.getSelectionModel().getSelectedItem().equals(lastSelected)) {
					lastSelected = treeView.getSelectionModel().getSelectedItem();
					selectionChanges(treeView.getSelectionModel().getSelectedItem());
				}
			} else {
				if (lastSelected != null) {
					lastSelected = null;
					selectionChanges(null);
				}
			}
		});

		this.setBottom(toolbar);
	}

	public TreeView<TreeObj> getTreeView() {
		return treeView;
	}

	public void selectionChanges(TreeItem<TreeObj> selected) {
		if (selected != null) {
			TreeObj obj = selected.getValue();

			if (obj instanceof Category) {
				if (selected.getChildren().size() < 7) {
					addMenu.setDisable(false);
					addMenu.getStyleClass().removeAll("disabled");
					addPage.setDisable(false);
					addPage.getStyleClass().removeAll("disabled");
				} else {
					addMenu.setDisable(true);
					addMenu.getStyleClass().add("disabled");
					addPage.setDisable(true);
					addPage.getStyleClass().add("disabled");
				}
			} else {
				addMenu.setDisable(true);
				addMenu.getStyleClass().add("disabled");
				addPage.setDisable(true);
				addPage.getStyleClass().add("disabled");
			}

			if (obj.getPosition() == -1) {
				delete.setDisable(true);
				delete.getStyleClass().add("disabled");
			} else {
				delete.setDisable(false);
				delete.getStyleClass().removeAll("disabled");
			}
		} else {
			addMenu.setDisable(true);
			addMenu.getStyleClass().add("disabled");
			addPage.setDisable(true);
			addPage.getStyleClass().add("disabled");
			delete.setDisable(true);
			delete.getStyleClass().add("disabled");
		}
	}
}
