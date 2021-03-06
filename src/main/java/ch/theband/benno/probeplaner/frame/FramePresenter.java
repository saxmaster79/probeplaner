package ch.theband.benno.probeplaner.frame;

import ch.theband.benno.probeplaner.ProbePlanerModel;
import ch.theband.benno.probeplaner.detail.DetailPresenter;
import ch.theband.benno.probeplaner.detail.DetailView;
import ch.theband.benno.probeplaner.rehearsals.RehearsalsPresenter;
import ch.theband.benno.probeplaner.rehearsals.RehearsalsView;
import ch.theband.benno.probeplaner.service.OpenService;
import ch.theband.benno.probeplaner.service.PdfFileExtractor;
import ch.theband.benno.probeplaner.service.SaveService;
import ch.theband.benno.probeplaner.treetable.LinesTablePresenter;
import ch.theband.benno.probeplaner.treetable.LinesTableView;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.*;

import javax.inject.Inject;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FramePresenter {

    @FXML
    private BorderPane west;
    @FXML
    private VBox east;

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
    LinesTableView linesTableView;
    @Inject
    RehearsalsView rehearsalsView;
    @Inject
    ProgressDialogView progressDialog;
    @Inject
    DetailView detailView;

    private Stage dialogStage;

    public FramePresenter() {
        super();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
    }

    public void initialize() {
        west.setCenter(linesTableView.getView());
        east.getChildren().add(rehearsalsView.getView());
        chooser.setInitialDirectory(new File("."));
        getDetailPresenter().setCurrentRehearsalEditedListener(getRehearsalsPresenter()::currentRehearsalEdited);
        getLinesTablePresenter().setCreateRehearsalCallback(getRehearsalsPresenter()::addRehearsal);
    }

    private LinesTablePresenter getLinesTablePresenter() {
        return (LinesTablePresenter)linesTableView.getPresenter();
    }

    private DetailPresenter getDetailPresenter() {
        return (DetailPresenter)detailView.getPresenter();
    }

    private RehearsalsPresenter getRehearsalsPresenter() {
        return (RehearsalsPresenter) rehearsalsView.getPresenter();
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
            saveService.setProbePlanerData(model.getProbePlanerData());
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

                model.setProbePlanerData(openService.getValue());
                model.setSavedFile(file);
            });
        }
    }

    private Window getWindow() {
        return west.getScene().getWindow();
    }

    @FXML
    public void doNew() {
        System.out.println("Not Implemented");
    }

    @FXML
    public void doExit() {
        Platform.exit();
    }

}
