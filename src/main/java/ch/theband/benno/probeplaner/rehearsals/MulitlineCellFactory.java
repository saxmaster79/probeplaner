package ch.theband.benno.probeplaner.rehearsals;

import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * CellFactory with Word-Wrapping
 */
public class MulitlineCellFactory<T> implements Callback<TableColumn<T, String>, TableCell<T, String>> {

    @Override
    public TableCell<T, String> call(
            TableColumn<T, String> param) {
        TableCell<T, String> cell = new TableCell<>();
        Text text = new Text();
        cell.setGraphic(text);
        cell.setPrefHeight(Control.USE_COMPUTED_SIZE);//64 -> Dann schrumpft die Zeile nicht mehr
        text.wrappingWidthProperty().bind(cell.widthProperty());
        text.textProperty().bind(cell.itemProperty());
        return cell;
    }
}
