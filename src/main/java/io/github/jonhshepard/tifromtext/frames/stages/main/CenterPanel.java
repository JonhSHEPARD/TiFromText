package io.github.jonhshepard.tifromtext.frames.stages.main;

import io.github.jonhshepard.tifromtext.objects.Page;
import io.github.jonhshepard.tifromtext.objects.PageLine;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JonhSHEPARD
 */
public abstract class CenterPanel extends BorderPane {

	private int size = 15;

	protected CenterPanel(Page page) {
		super();

		TextFlow textFlow = new TextFlow();
		textFlow.setVisible(false);
		ScrollPane textFlowPane = new ScrollPane();
		textFlowPane.setContent(textFlow);

		TextArea textArea = new TextArea();
		textArea.setStyle("-fx-font: 15px monospace;");
		textArea.setWrapText(true);
		textArea.setEditable(true);
		textArea.textProperty().addListener((observable, oldValue, newValue) -> {
			String[] split = newValue.split("\n");
			boolean cancel = false;
			for (String s : split) {
				if (s.length() > 25) {
					cancel = true;
					break;
				}
			}
			if (cancel) {
				textArea.setText(oldValue);
			}
		});
		String text = "";
		for (int i = 0; i < page.getLines().size(); i++) {
			text += page.getLines().get(i).getContent();
			text += (i + 1 < page.getLines().size()) ?  "\n" : "";
		}
		textArea.setText(text);
		this.setCenter(textArea);

		HBox toolbar = new HBox();
		toolbar.setAlignment(Pos.CENTER_RIGHT);
		toolbar.setPadding(new Insets(0, 5, 0, 5));
		toolbar.setSpacing(10);
		toolbar.setMinHeight(40);
		HBox.setHgrow(toolbar, Priority.ALWAYS);

		Button sizeMinus = new Button("Zoom -");
		sizeMinus.getStyleClass().setAll("btn", "btn-sm", "btn-danger");
		sizeMinus.setOnMouseClicked(event -> {
			size--;
			if(size < 14) size = 14;
			textArea.setStyle("-fx-font: " + size + "px monospace;");
			textFlow.setStyle("-fx-font: " + size + "px monospace;");
		});
		toolbar.getChildren().add(sizeMinus);

		Button sizePlus = new Button("Zoom +");
		sizePlus.getStyleClass().setAll("btn", "btn-sm", "btn-primary");
		sizePlus.setOnMouseClicked(event -> {
			size++;
			if(size > 30) size = 30;
			textArea.setStyle("-fx-font: " + size + "px monospace;");
			textFlow.setStyle("-fx-font: " + size + "px monospace;");
		});
		toolbar.getChildren().add(sizePlus);

		Button breakBtn = new Button("Saut de page");
		breakBtn.getStyleClass().setAll("btn", "btn-sm", "btn-success");
		breakBtn.setOnMouseClicked(event -> textArea.appendText("\n/Break/"));
		toolbar.getChildren().add(breakBtn);

		Button presee = new Button("Prévisualisation");
		presee.getStyleClass().setAll("btn", "btn-sm", "btn-info");
		presee.setOnMouseClicked(event -> {
			if (textArea.isVisible()) {
				textArea.setVisible(false);
				textFlow.setVisible(true);
				textFlow.getChildren().clear();
				this.setCenter(textFlowPane);
				presee.setText("Edition");
				presee.getStyleClass().setAll("btn", "btn-sm", "btn-warning");

				boolean first = true;
				for (PageLine p : formatText(textArea.getText())) {
					if (!(first && p.getContent().length() == 0)) {
						String txt = (p.getContent().length() == 0) ? "----" : p.getContent();
						Text t = new Text(txt + "\n");
						if (first) {
							t.setFill(Color.GREEN);

							Tooltip tp = new Tooltip("Début de page");
							Tooltip.install(t, tp);

							first = false;
						} else if (p.isBreakLine()) {
							t.setFill(Color.RED);
							t.setText(t.getText() + "\n\n");

							Tooltip tp = new Tooltip("Fin de page");
							Tooltip.install(t, tp);

							first = true;
						}

						textFlow.getChildren().addAll(t);
						if (textFlowPane.getVvalue() > 0.9) textFlowPane.setVvalue(textFlowPane.getVmax());
					}
				}
			} else {
				textArea.setVisible(true);
				textFlow.setVisible(false);
				textFlow.getChildren().clear();
				this.setCenter(textArea);
				presee.setText("Prévisualisation");
				presee.getStyleClass().setAll("btn", "btn-sm", "btn-info");
			}
		});
		toolbar.getChildren().add(presee);

		Button save = new Button("Enregistrer");
		save.getStyleClass().setAll("btn", "btn-sm", "btn-info");
		save.setOnMouseClicked(event -> {
			page.setLines(formatText(textArea.getText()));
			save(page);
		});
		toolbar.getChildren().add(save);

		this.setBottom(toolbar);
	}

	private List<PageLine> formatText(String text) {
		List<PageLine> lines = new ArrayList<>();

		int currentIndex = 1;
		String[] linesT = text.split("\n");
		for (int i = 0; i < linesT.length; i++) {
			String txt = linesT[i];
			if (txt.length() > 25) txt = txt.substring(0, 25);

			if (currentIndex == 9
					|| txt.equalsIgnoreCase("/Break/")
					|| i + 1 == linesT.length) {
				lines.add(new PageLine(txt, true));
				currentIndex = 0;
			} else {
				lines.add(new PageLine(txt));
				currentIndex++;
			}
		}

		return lines;
	}

	public abstract void save(Page p);
}
