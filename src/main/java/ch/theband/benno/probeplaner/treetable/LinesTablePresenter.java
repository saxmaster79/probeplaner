package ch.theband.benno.probeplaner.treetable;

import java.util.Objects;

import javax.inject.Inject;

import com.google.common.collect.ImmutableList;

import ch.theband.benno.probeplaner.ProbePlanerModel;
import ch.theband.benno.probeplaner.model.Play;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

public class LinesTablePresenter {

	@FXML
	private TreeTableView<TreeTableRow> treeTable;
	@FXML
	private TreeTableColumn<TreeTableRow, String> name;

	@Inject
	ProbePlanerModel model;

	public void initialize() {
		model.playProperty().addListener((observable, oldValue, newValue) -> createTreeTable(newValue));
		treeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private void createTreeTable(Play play) {
		treeTable.setRoot(null);
		if (play != null) {
			name.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
			name.setCellFactory(col -> new TextFieldTreeTableCell<>());
			LineFactory service = new LineFactory(play);
			service.setOnSucceeded(t -> {
				TreeItem<TreeTableRow> root = service.getValue();
				root.setExpanded(true);
				treeTable.setRoot(root);
			});
			service.setOnFailed(t -> {
				System.out.println("Failed!" + t.getSource().getException());
				throw new RuntimeException(t.getSource().getException());
			});
			service.start();

			service.getHeaders().stream().map(this::createTreeTableColumn).forEachOrdered(treeTable.getColumns()::add);
		}
	}

	private TreeTableColumn<TreeTableRow, Number> createTreeTableColumn(String columnTitle) {
		TreeTableColumn<TreeTableRow, Number> column = new TreeTableColumn<>();
		column.setText(columnTitle);
		column.setCellValueFactory(createValueFactory(columnTitle));
		column.setCellFactory(createCellFactory());
		column.setSortable(false);
		column.setEditable(true);
		return column;
	}

	private Callback<TreeTableColumn<TreeTableRow, Number>, TreeTableCell<TreeTableRow, Number>> createCellFactory() {
		return col -> new BlueIntegerTableCell();

	}

	private Callback<CellDataFeatures<TreeTableRow, Number>, ObservableValue<Number>> createValueFactory(String title) {
		return cellDataFeatures -> {

			TreeTableRow row = cellDataFeatures.getValue().getValue();
			ObservableValue<Number> simpleIntegerProperty;

			if (row != null && row.getLines().get(title) != null) {
				simpleIntegerProperty = new SimpleIntegerProperty(row.getLines().get(title));
				simpleIntegerProperty.addListener((observable, oldValue, newValue) -> row.getLines().put(title, (Integer) newValue));
			} else {
				simpleIntegerProperty = new SimpleIntegerProperty();
			}
			return simpleIntegerProperty;

		};
	}

	private static final class BlueIntegerTableCell extends TextFieldTreeTableCell<TreeTableRow, Number> {
		BlueIntegerTableCell() {
			super(createConverter());
			setAlignment(Pos.BASELINE_RIGHT);
		}

		private static StringConverter<Number> createConverter() {
			IntegerStringConverter x = new IntegerStringConverter();
			return new StringConverter<Number>() {

				@Override
				public String toString(Number object) {
					return Objects.toString(object, "");
				}

				@Override
				public Number fromString(String string) {
					return x.fromString(string);
				}
			};
		}

		@Override
		public void updateItem(Number item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty || item.intValue() == 0) {
				setText(null);
				setStyle("");
			} else {
				setText(item.toString());
				setStyle("-fx-background-color: DeepSkyBlue ");
			}

		}
	}

	@FXML
	public void createRehearsal() {
		System.out.println(treeTable.getSelectionModel().getSelectedItems());
	}

	@FXML
	public void delete() {
		TreeTableViewSelectionModel<TreeTableRow> sel = treeTable.getSelectionModel();
		for (TreeItem<TreeTableRow> item : ImmutableList.copyOf(sel.getSelectedItems())) {
			if (item != null) {
				item.getParent().getChildren().remove(item);
			}
		}
		sel.clearSelection();
	}

}
