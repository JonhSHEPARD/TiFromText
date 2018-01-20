package io.github.jonhshepard.tifromtext.frames.stages.main;

import io.github.jonhshepard.tifromtext.objects.TreeObj;
import javafx.scene.control.TreeCell;

/**
 * @author JonhSHEPARD
 */
class LeftPanelTreeCell extends TreeCell<TreeObj> {

	LeftPanelTreeCell() {
	}

	@Override
	public void startEdit() {
		super.startEdit();
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getItem().getName());
		setGraphic(getTreeItem().getGraphic());
	}

	@Override
	public void updateItem(TreeObj item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				setText(null);
			} else {
				setText(getString());
				setGraphic(getTreeItem().getGraphic());
			}
		}
	}

	private String getString() {
		return getItem() == null ? "" : getItem().getName();
	}
}
