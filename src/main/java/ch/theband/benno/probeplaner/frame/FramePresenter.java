package ch.theband.benno.probeplaner.frame;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import ch.theband.benno.probeplaner.ProbePlanerModel;
import ch.theband.benno.probeplaner.rehearsals.RehearsalsView;
import ch.theband.benno.probeplaner.service.OpenService;
import ch.theband.benno.probeplaner.service.PdfFileExtractor;
import ch.theband.benno.probeplaner.service.SaveService;
import ch.theband.benno.probeplaner.treetable.LinesTableView;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.*;

public class FramePresenter {

    @FXML
    private VBox east;
    @FXML
    private VBox north;
    @FXML
    private VBox south;

    private final FileChooser chooser = new FileChooser();

    @Inject
    PdfFileExtractor importService;

    @Inject
    SaveService saveService;
    @Inject
    OpenService openService;

    @Inject
    ProbePlanerModel model;
    @Inject
    LinesTableView northView;
    @Inject
    RehearsalsView rehearsalsView;
    @Inject
    ProgressDialogView progressDialog;
    private Stage dialogStage;

    public FramePresenter() {
        super();
    }

    public void initialize() {
        north.getChildren().add(northView.getView());
        rehearsalsView = new RehearsalsView();
        east.getChildren().add(rehearsalsView.getView());
        chooser.setInitialDirectory(new File("."));
    }

    @FXML
    public void doImport() {
        importService.reset();
        importService.setOnSucceeded(evt -> {
            dialogStage.hide();
            model.setPlay(importService.getValue());
        });
        importService.setOnFailed(evt -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Fehler beim importieren");
            alert.setContentText(evt.getSource().getException().getLocalizedMessage());
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", evt.getSource().getException());
            alert.showAndWait();
            dialogStage.hide();
        });

        importService.start();
        createProgressDialog();
        activateProgressBar(importService.progressProperty());
    }

    private void createProgressDialog() {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        // PROGRESS BAR
        final Label label = new Label();
        label.setText("alerto");

        Scene scene = new Scene(progressDialog.getView());
        dialogStage.setScene(scene);
    }

    public void activateProgressBar(ObservableValue<? extends Number> progressProperty) {
        ((ProgressDialogPresenter) progressDialog.getPresenter()).progressBar.progressProperty().bind(progressProperty);

        dialogStage.show();
    }

    @FXML
    public void saveAs() {
        File file = chooser.showSaveDialog(getWindow());
        if (file != null) {
            model.setSavedFile(file);
            save();
        }
    }

    @FXML
    public void save() {
        if (model.getSavedFile() == null) {
            saveAs();
        } else {
            saveService.reset();
            saveService.setFile(model.getSavedFile());
            saveService.setPlay(model.getPlay());
            saveService.start();
        }
    }

    @FXML
    public void open() {
        File file = chooser.showOpenDialog(getWindow());
        if (file != null) {
            openService.reset();
            openService.setFile(file);
            openService.start();
            openService.setOnSucceeded(evt -> {
                model.setPlay(openService.getValue());
                model.setSavedFile(file);
            });
        }
    }

    private Window getWindow() {
        return north.getScene().getWindow();
    }

    @FXML
    public void doNew() {
        // model.setPlay(new Play(name, roles, acts));
    }

    @FXML
    public void doExit() {
        Platform.exit();
    }
}
