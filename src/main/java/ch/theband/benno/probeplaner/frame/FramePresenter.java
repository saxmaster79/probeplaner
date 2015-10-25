package ch.theband.benno.probeplaner.frame;

import java.io.File;

import javax.inject.Inject;

import ch.theband.benno.probeplaner.ProbePlanerModel;
import ch.theband.benno.probeplaner.service.OpenService;
import ch.theband.benno.probeplaner.service.PdfFileExtractor;
import ch.theband.benno.probeplaner.service.SaveService;
import ch.theband.benno.probeplaner.treetable.LinesTableView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

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

	public void initialize() {
		north.getChildren().add(northView.getView());
		chooser.setInitialDirectory(new File("."));
	}

	@FXML
	public void doImport() {
		importService.reset();
		importService.setOnSucceeded(evt -> model.setPlay(importService.getValue()));
		importService.start();
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
