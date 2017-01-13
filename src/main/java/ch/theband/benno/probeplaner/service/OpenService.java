package ch.theband.benno.probeplaner.service;

import ch.theband.benno.probeplaner.model.Play;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class OpenService extends ErrorHandlingService<Play> {
	private final Property<File> file = new SimpleObjectProperty<>();

	@Override
	protected Task<Play> createTask() {
		return new Task<Play>() {

			@Override
            protected Play call() throws IOException, ClassNotFoundException {
                Path path = file.getValue().toPath();
				System.out.println("Reading file " + path);
                String xml = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                XStream xstream = new XStream(new StaxDriver());
                Play readPlay = (Play) xstream.fromXML(xml);
                return readPlay;
            }

		};
	}

	public void setFile(File file) {
		this.file.setValue(file);
	}

}
