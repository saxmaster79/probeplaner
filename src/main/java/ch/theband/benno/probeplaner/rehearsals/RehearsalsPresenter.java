package ch.theband.benno.probeplaner.rehearsals;

import javax.inject.Inject;

import ch.theband.benno.probeplaner.detail.DetailView;
import ch.theband.benno.probeplaner.model.Rehearsal;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class RehearsalsPresenter {
    @FXML
    private ListView<Rehearsal> rehearsals;

    @FXML
    private SplitPane splitPane;
    @Inject
    DetailView detailsView;

    public RehearsalsPresenter() {
        super();
    }

    public void initialize() {
        splitPane.getItems().add(0, detailsView.getView());
    }
}
