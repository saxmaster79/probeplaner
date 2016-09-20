package ch.theband.benno.probeplaner.detail;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.theband.benno.probeplaner.ProbePlanerModel;
import ch.theband.benno.probeplaner.model.PartOfPlay;
import ch.theband.benno.probeplaner.model.Role;
import ch.theband.benno.probeplaner.model.Scene;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

import javax.inject.Inject;

public class DetailPresenter {

    @Inject
    ProbePlanerModel model;
    @FXML
    private ListView<Role> list;
    @FXML
    private DatePicker date;
    @FXML
    ComboBox<LocalTime> time;

    @FXML
    public void initialize() {
        time.getItems().add(LocalTime.of(17, 0));
        time.getItems().add(LocalTime.of(18, 0));
        time.getItems().add(LocalTime.of(18, 30));
        time.getItems().add(LocalTime.of(19, 0));
        time.getItems().add(LocalTime.of(19, 30));
    }

    public boolean createRehearsal(List<PartOfPlay> scenes) {
        Stream<Role> roles = scenes.stream().filter(part -> part instanceof Scene).map(part -> (Scene) part).flatMap(scene -> scene.getPages().stream()).flatMap(page -> page.getLines().entrySet().stream()).filter(e -> e.getValue() != null && e.getValue() > 0).map(e -> e.getKey());
        list.getItems().clear();
        list.getItems().addAll(roles.collect(Collectors.toCollection(()->new TreeSet<>(model.getPlay().rolesComparator()))));
        return true;
    }
}
