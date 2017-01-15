package ch.theband.benno.probeplaner.rehearsals;

import ch.theband.benno.probeplaner.ProbePlanerModel;
import ch.theband.benno.probeplaner.detail.DetailView;
import ch.theband.benno.probeplaner.model.*;
import com.google.common.collect.Ordering;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RehearsalsPresenter implements Initializable {
    private DateTimeFormatter formatter;
    @FXML
    private TableView<Rehearsal> rehearsals;
    @FXML
    private SplitPane splitPane;
    @FXML
    private WhoCellValueFactory whoCellValueFactory;
    @FXML
    private TableColumn<Rehearsal, LocalDateTime> date;
    @Inject
    DetailView detailsView;

    @Inject
    ProbePlanerModel model;

    public RehearsalsPresenter() {
        super();
    }

    @Override//BI: Anscheindend brauchts das noch um an das Property dateFormat zu kommen
    public void initialize(URL location, ResourceBundle resources) {
        formatter = DateTimeFormatter.ofPattern(resources.getString("dateFormat"));
        initialize();
    }

    public void initialize() {
        splitPane.getItems().add(0, detailsView.getView());
        model.playProperty().addListener((observable, oldValue, newValue) -> updateComparator(newValue));

        date.setCellFactory(col -> new TableCell<Rehearsal, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {

                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format(item.format(formatter)));
            }
        });
    }

    private void updateComparator(Play newValue) {
        Comparator<? super Role> comparator = newValue != null ? newValue.rolesComparator() : Ordering.usingToString();
        whoCellValueFactory.setComparator(comparator);
    }

    public Void saveRehearsal(Rehearsal rehearsal) {
        rehearsals.getItems().add(rehearsal);
        return null;
    }


    public static class WhoCellValueFactory implements javafx.util.Callback<TableColumn.CellDataFeatures<Rehearsal, String>, javafx.beans.value.ObservableValue<String>> {

        private Comparator<? super Role> comparator = Ordering.usingToString();

        @Override
        public ObservableValue<String> call(TableColumn.CellDataFeatures<Rehearsal, String> param) {
            List<PartOfPlay> what = param.getValue().getWhat();
            final String value = what.stream().flatMap(this::getRoles).distinct().sorted(comparator).map(r -> r.getName()).collect(Collectors.joining(", "));

            return new ReadOnlyStringWrapper(value);
        }

        private Stream<Role> getRoles(PartOfPlay partOfPlay) {
            if (partOfPlay instanceof Scene) {
                return getRoles((Scene) partOfPlay);
            }
            if (partOfPlay instanceof Act) {
                return getRoles((Act) partOfPlay);
            }
            return Stream.empty();
        }

        private Stream<Role> getRoles(Scene scene) {
            return scene.getPages().stream().flatMap(p -> getRoles(p).stream());
        }

        private Stream<Role> getRoles(Act act) {
            return act.getScenes().stream().flatMap(this::getRoles);
        }

        private Collection<Role> getRoles(Page page) {
            return page.getLines().keySet();
        }

        public void setComparator(Comparator<? super Role> comparator) {
            this.comparator = comparator;
        }
    }


}
