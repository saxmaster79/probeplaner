package ch.theband.benno.probeplaner.service;

import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.theband.benno.probeplaner.model.Play;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

public class OpenService extends ErrorHandlingService<Play> {
	private final Property<File> file = new SimpleObjectProperty<>();

	@Override
	protected Task<Play> createTask() {
		return new Task<Play>() {

			@Override
			protected Play call() throws Exception {
				Path path = file.getValue().toPath();
				System.out.println("Reading file " + path);
				InputStream inputStream = Files.newInputStream(path);
				try (ObjectInputStream is = new ObjectInputStream(inputStream)) {
					Play readPlay = (Play) is.readObject();
					System.out.println("Read play: " + readPlay);
					return readPlay;
				}

			}

		};
	}

	public void setFile(File file) {
		this.file.setValue(file);
	}

}
