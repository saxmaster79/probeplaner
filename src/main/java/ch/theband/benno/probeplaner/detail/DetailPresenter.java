package ch.theband.benno.probeplaner.detail;

import ch.theband.benno.probeplaner.ProbePlanerModel;
import ch.theband.benno.probeplaner.model.PartOfPlay;
import ch.theband.benno.probeplaner.model.Rehearsal;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DetailPresenter {

    @Inject
    private ProbePlanerModel model;
    @FXML
    private TextArea who;
    @FXML
    private TextArea remark;
    @FXML
    private DatePicker date;
    @FXML
    private ComboBox<LocalTime> time;
    private Runnable currentRehearsalEditedListener;

    @FXML
    public void initialize() {
        date.setValue(LocalDate.now());
        time.getItems().add(LocalTime.of(17, 0));
        time.getItems().add(LocalTime.of(18, 0));
        time.getItems().add(LocalTime.of(18, 30));
        time.getItems().add(LocalTime.of(19, 0));
        time.getItems().add(LocalTime.of(19, 30));
        updateControls(null);

        remark.textProperty().addListener((observable, oldValue, newValue) -> {
            if (model.getCurrentRehearsal() != null) model.getCurrentRehearsal().setRemark(newValue);
            notifyCurrentRehearsalEdited();
        });
        who.textProperty().addListener((observable, oldValue, newValue) -> {
            if (model.getCurrentRehearsal() != null) model.getCurrentRehearsal().setWho(newValue);
            notifyCurrentRehearsalEdited();
        });
        date.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateDateTime();
        });

        model.currentRehearsalProperty().addListener((obs, o, n) -> {
            updateControls(n);
        });
    }

    private void updateControls(Rehearsal rehearsal) {
        if (rehearsal == null) {
            date.setDisable(true);
            date.setValue(null);
            time.setDisable(true);
            time.setValue(null);
            who.setDisable(true);
            who.setText(null);
            remark.setDisable(true);
            remark.setText(null);
        } else {
            List<PartOfPlay> scenes = rehearsal.getWhat();
            date.setDisable(false);
            date.setValue(rehearsal.getStartTime().toLocalDate());
            time.setDisable(false);
            time.setValue(rehearsal.getStartTime().toLocalTime());
            who.setDisable(false);
            who.setText(rehearsal.getWho());
            remark.setDisable(false);
            remark.setText(rehearsal.getRemark());
        }
    }

    private void updateDateTime() {
        if (model.getCurrentRehearsal() != null) {
            if (date.getValue() != null && time.getValue() != null) {
                model.getCurrentRehearsal().setStartTime(LocalDateTime.of(date.getValue(), time.getValue()));
                notifyCurrentRehearsalEdited();
            }
        }
    }

    private void notifyCurrentRehearsalEdited() {
        currentRehearsalEditedListener.run();
    }

    public void setCurrentRehearsalEditedListener(Runnable currentRehearsalEditedListener) {
        this.currentRehearsalEditedListener = currentRehearsalEditedListener;
    }

    @FXML
    public void save() {
        //todo lösche
    }

}
