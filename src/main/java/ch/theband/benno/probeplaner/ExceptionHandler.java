package ch.theband.benno.probeplaner;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ExceptionHandler implements UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable original) {
        original.printStackTrace();
        Throwable ex = findReasonableCausedBy(original);
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR, "Fehler");
            alert.setTitle("Exception Dialog");
            alert.setHeaderText("Fehler");
            alert.setContentText(ex.getLocalizedMessage() == null ? ex.getClass().getSimpleName() : ex.getLocalizedMessage());
            //alert.setOnHidden(x -> System.exit(-1));

            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);

            alert.showAndWait();
        });

    }

    private Throwable findReasonableCausedBy(Throwable ex) {
        if (ex.getCause() != null) {
            if (ex instanceof InvocationTargetException) {
                return findReasonableCausedBy(ex.getCause());
            } else if (RuntimeException.class == ex.getClass()) {
                return findReasonableCausedBy(ex.getCause());
            }
        }
        return ex;
    }

}
