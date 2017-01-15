package ch.theband.benno.probeplaner.detail;

import ch.theband.benno.probeplaner.ProbePlanerModel;
import ch.theband.benno.probeplaner.model.PartOfPlay;
import ch.theband.benno.probeplaner.model.Rehearsal;
import ch.theband.benno.probeplaner.model.Role;
import ch.theband.benno.probeplaner.model.Scene;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.util.Callback;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DetailPresenter {

    @Inject
    ProbePlanerModel model;
    @FXML
    private TextArea who;
    @FXML
    private TextArea remark;
    @FXML
    private DatePicker date;
    @FXML
    ComboBox<LocalTime> time;
    private Callback<Rehearsal, Void> saveRehearsalCallback;
    private List<PartOfPlay> scenes;

    @FXML
    public void initialize() {
        date.setValue(LocalDate.now());
        time.getItems().add(LocalTime.of(17, 0));
        time.getItems().add(LocalTime.of(18, 0));
        time.getItems().add(LocalTime.of(18, 30));
        time.getItems().add(LocalTime.of(19, 0));
        time.getItems().add(LocalTime.of(19, 30));
        time.setValue(LocalTime.of(19, 30));
    }

    @FXML
    public void save() {
        LocalDateTime when = LocalDateTime.of(date.getValue(), time.getValue());
        Rehearsal rehearsal = new Rehearsal(when, scenes, remark.getText());
        saveRehearsalCallback.call(rehearsal);


    }

    public void setSaveRehearsalCallback(Callback<Rehearsal, Void> saveRehearsalCallback) {
        this.saveRehearsalCallback = saveRehearsalCallback;
    }

    public boolean createRehearsal(List<PartOfPlay> scenes) {
        Stream<Role> roles = scenes.stream().filter(part -> part instanceof Scene).map(part -> (Scene) part).flatMap(scene -> scene.getPages().stream()).flatMap(page -> page.getLines().entrySet().stream()).filter(e -> e.getValue() != null && e.getValue() > 0).map(e -> e.getKey());
        who.clear();
        who.setText(roles.distinct().sorted(model.getPlay().rolesComparator()).map(Role::getName).collect(Collectors.joining(", ")));
        this.scenes = scenes;
        return true;
    }
}
