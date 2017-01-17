package ch.theband.benno.probeplaner.rehearsals;

import ch.theband.benno.probeplaner.ProbePlanerModel;
import ch.theband.benno.probeplaner.detail.DetailView;
import ch.theband.benno.probeplaner.model.Play;
import ch.theband.benno.probeplaner.model.Rehearsal;
import ch.theband.benno.probeplaner.model.Role;
import ch.theband.benno.probeplaner.table.TableUtils;
import com.google.common.collect.Ordering;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.ResourceBundle;

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
    @FXML
    private TableColumn<Rehearsal, String> who;
    @Inject
    private DetailView detailsView;

    @Inject
    private ProbePlanerModel model;

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
                    setText(item.format(formatter));
            }
        });
        who.setCellFactory(new MulitlineCellFactory<>());
        rehearsals.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableUtils.installCopyPasteHandler(rehearsals);
    }

    private void updateComparator(Play newValue) {
        Comparator<? super Role> comparator = newValue != null ? newValue.rolesComparator() : Ordering.usingToString();
        whoCellValueFactory.setComparator(comparator);
    }

    public Void addRehearsal(Rehearsal rehearsal) {
        model.getPlay();
        rehearsals.getItems().add(rehearsal);

        return null;
    }


}
