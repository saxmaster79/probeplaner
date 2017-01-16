package ch.theband.benno.probeplaner.treetable;

import ch.theband.benno.probeplaner.table.TableUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.StringTokenizer;

import static ch.theband.benno.probeplaner.table.TableUtils.COPY;
import static ch.theband.benno.probeplaner.table.TableUtils.PASTE;

public class TreeTableUtils {

    private static NumberFormat numberFormatter = NumberFormat.getNumberInstance();


    /**
     * Install the keyboard handler:
     * + CTRL + C = copy to clipboard
     * + CTRL + V = paste to clipboard
     *
     * @param table
     */
    public static void installCopyPasteHandler(TreeTableView<?> table) {

        // install copy/paste keyboard handler
        table.setOnKeyPressed(new TableKeyEventHandler());

    }

    /**
     * Copy/Paste keyboard event handler.
     * The handler uses the keyEvent's source for the clipboard data. The source must be of type TreeTableView.
     */
    private static class TableKeyEventHandler implements EventHandler<KeyEvent> {

        public void handle(final KeyEvent keyEvent) {
            if (COPY.match(keyEvent)) {
                if (keyEvent.getSource() instanceof TreeTableView) {
                    copySelectionToClipboard((TreeTableView<?>) keyEvent.getSource());
                    keyEvent.consume();
                }

            } else if (PASTE.match(keyEvent)) {
                if (keyEvent.getSource() instanceof TreeTableView) {
                    pasteFromClipboard((TreeTableView<?>) keyEvent.getSource());
                    keyEvent.consume();
                }
            }
        }
    }

    /**
     * Get table selection and copy it to the clipboard.
     *
     */
    private static <S> void copySelectionToClipboard(TreeTableView<S> table) {
        StringBuilder clipboardString = new StringBuilder();
        ObservableList<TreeTablePosition<S, ?>> positionList = table.getSelectionModel().getSelectedCells();
        for (TreeTablePosition<S, ?> position : positionList) {
            int row = position.getRow();
            for (TreeTableColumn<S, ?> col : table.getColumns()) {
                ObservableValue<?> observableValue = col.getCellObservableValue(row);
                final String text = TableUtils.getString(observableValue);

                // add new item to clipboard
                clipboardString.append(text);
                clipboardString.append('\t');

            }
            clipboardString.append('\n');
        }

        // create clipboard content
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(clipboardString.toString());

        // set clipboard content
        Clipboard.getSystemClipboard().setContent(clipboardContent);


    }

    private static void pasteFromClipboard(TreeTableView<?> table) {
        // abort if there's not cell selected to start with
        if (table.getSelectionModel().getSelectedCells().size() == 0) {
            return;
        }

        // get the cell position to start with
        TreeTablePosition pasteCellPosition = table.getSelectionModel().getSelectedCells().get(0);

        System.out.println("Pasting into cell " + pasteCellPosition);
        String pasteString = Clipboard.getSystemClipboard().getString();
        System.out.println(pasteString);

        int rowClipboard = -1;

        StringTokenizer rowTokenizer = new StringTokenizer(pasteString, "\n");
        while (rowTokenizer.hasMoreTokens()) {
            rowClipboard++;
            String rowString = rowTokenizer.nextToken();
            StringTokenizer columnTokenizer = new StringTokenizer(rowString, "\t");
            int colClipboard = -1;
            while (columnTokenizer.hasMoreTokens()) {
                colClipboard++;
                // get next cell data from clipboard
                String clipboardCellContent = columnTokenizer.nextToken();

                // calculate the position in the table cell
                int rowTable = pasteCellPosition.getRow() + rowClipboard;
                int colTable = pasteCellPosition.getColumn() + colClipboard;

                // skip if we reached the end of the table
                if (rowTable >= table.getExpandedItemCount()) {
                    continue;
                }
                if (colTable >= table.getColumns().size()) {
                    continue;
                }

                // get cell
                TreeTableColumn tableColumn = table.getColumns().get(colTable);
                ObservableValue observableValue = tableColumn.getCellObservableValue(rowTable);

                System.out.println(rowTable + "/" + colTable + ": " + observableValue);

                // TODO: handle boolean, etc
                if (observableValue instanceof DoubleProperty) {

                    try {

                        double value = numberFormatter.parse(clipboardCellContent).doubleValue();
                        ((DoubleProperty) observableValue).set(value);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else if (observableValue instanceof IntegerProperty) {

                    try {

                        int value = NumberFormat.getInstance().parse(clipboardCellContent).intValue();
                        ((IntegerProperty) observableValue).set(value);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else if (observableValue instanceof StringProperty) {

                    ((StringProperty) observableValue).set(clipboardCellContent);

                } else {

                    System.out.println("Unsupported observable value: " + observableValue);

                }

                System.out.println(rowTable + "/" + colTable);
            }

        }

    }

}