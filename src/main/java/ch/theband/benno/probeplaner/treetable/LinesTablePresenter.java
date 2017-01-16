package ch.theband.benno.probeplaner.treetable;

import ch.theband.benno.probeplaner.ProbePlanerModel;
import ch.theband.benno.probeplaner.model.PartOfPlay;
import ch.theband.benno.probeplaner.model.Play;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

public class LinesTablePresenter {

    @FXML
    private TreeTableView<TreeTableRow> treeTable;
    @FXML
    private TreeTableColumn<TreeTableRow, String> treeColumn;
    @FXML
    private MenuItem createScene;
    @FXML
    private MenuItem createAct;
    @FXML
    private MenuItem delete;

    @Inject
    ProbePlanerModel model;
    private Callback<List<PartOfPlay>, Boolean> createRehearsal;

    public void initialize() {
        model.playProperty().addListener((observable, oldValue, newValue) -> createTreeTable(newValue));
        treeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TreeTableUtils.installCopyPasteHandler(treeTable);
    }

    private void createTreeTable(Play play) {
        resetColumns();
        treeTable.setRoot(null);
        if (play != null) {
            treeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
            treeColumn.setCellFactory(col -> new TextFieldTreeTableCell<>());
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

    private void resetColumns() {
        ObservableList<TreeTableColumn<TreeTableRow, ?>> cols = treeTable.getColumns();
        if (cols.size() > 1) {
            cols.remove(1, cols.size());
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
                simpleIntegerProperty.addListener((observable, oldValue, newValue) -> {
                    row.getLines().put(title, (Integer) newValue);
                    model.sumUpParents(cellDataFeatures.getValue());

                });
            } else {
                simpleIntegerProperty = new SimpleIntegerProperty();
            }
            return simpleIntegerProperty;

        };
    }

    @FXML
    public void adjustContextMenu() {
        System.out.println("adjustContextMEnu");
    }

    @FXML
    public void adjustContextMenu(Event event) {
        List<TreeItem<TreeTableRow>> selectedItems = getSelectedItems();
        createScene.setVisible(!selectedItems.isEmpty() && model.onlyPages(selectedItems));
        createAct.setVisible(!selectedItems.isEmpty() && model.onlyScenes(selectedItems));
        delete.setVisible(!selectedItems.isEmpty());
    }

    private List<TreeItem<TreeTableRow>> getSelectedItems() {
        TreeTableViewSelectionModel<TreeTableRow> sel = treeTable.getSelectionModel();
        return sel.getSelectedItems();

    }

    public void setCreateRehearsalCallback(Callback<List<PartOfPlay>, Boolean> createRehearsal) {
        this.createRehearsal = createRehearsal;
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
        List<PartOfPlay> scenes = model.getScenes(treeTable.getSelectionModel().getSelectedItems());
        createRehearsal.call(scenes);
    }

    @FXML
    public void createScene() {
        TreeItem<TreeTableRow> selectedItem = treeTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            model.createScene(selectedItem);
        }
    }

    @FXML
    public void createAct() {
        TreeItem<TreeTableRow> selectedItem = treeTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            model.createAct(selectedItem);
        }
    }

    @FXML
    public void delete() {
        ObservableList<TreeItem<TreeTableRow>> selectedItems = treeTable.getSelectionModel().getSelectedItems();
        model.delete(selectedItems);
        treeTable.getSelectionModel().clearSelection();
    }

}
