package ch.theband.benno.probeplaner.service;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.theband.benno.probeplaner.model.Play;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

public class SaveService extends ErrorHandlingService<Void> {
	private final Property<Play> play = new SimpleObjectProperty<>();
	private final Property<File> file = new SimpleObjectProperty<>();

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {

			@Override
			protected Void call() throws IOException {
				Path path = file.getValue().toPath();
				System.out.println("Saving file " + path);
				OutputStream outputStream = Files.newOutputStream(path);
				try (ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
					oos.writeObject(play.getValue());
				}
				System.out.println("Saved file");
				return null;
			}

		};
	}

	public void setPlay(Play play) {
		this.play.setValue(play);

	}

	public Play getPlay() {
		return play.getValue();
	}

	public Property<Play> playProperty() {
		return play;
	}

	public void setFile(File file) {
		this.file.setValue(file);
	}

}
