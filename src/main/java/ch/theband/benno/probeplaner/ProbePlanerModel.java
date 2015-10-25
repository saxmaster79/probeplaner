package ch.theband.benno.probeplaner;

import java.io.File;

import ch.theband.benno.probeplaner.model.Play;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ProbePlanerModel {
	private final SimpleObjectProperty<Play> play = new SimpleObjectProperty<>();
	private final SimpleObjectProperty<File> savedFile = new SimpleObjectProperty<>();

	public Play getPlay() {
		return play.get();
	}

	public void setPlay(Play play) {
		this.play.set(play);
	}

	public ObjectProperty<Play> playProperty() {
		return play;
	}

	public File getSavedFile() {
		return savedFile.get();
	}

	public void setSavedFile(File savedFile) {
		this.savedFile.set(savedFile);
	}

	public ObjectProperty<File> savedFileProperty() {
		return savedFile;
	}

}
