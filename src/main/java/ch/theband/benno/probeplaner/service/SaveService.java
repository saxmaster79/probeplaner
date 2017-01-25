package ch.theband.benno.probeplaner.service;

import ch.theband.benno.probeplaner.model.ProbePlanerData;
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

public class SaveService extends ErrorHandlingService<Void> {
    private final Property<ProbePlanerData> probePlanerData = new SimpleObjectProperty<>();
    private final Property<File> file = new SimpleObjectProperty<>();

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws IOException {
                Path path = file.getValue().toPath();
                System.out.println("Saving file " + path);

                XStream xstream = new XStream(new StaxDriver());
                String xml = xstream.toXML(probePlanerData.getValue());
                Files.write(path, xml.getBytes(StandardCharsets.UTF_8));
                System.out.println("Saved file");
                return null;
            }

        };
    }

    public void setProbePlanerData(ProbePlanerData play) {
        this.probePlanerData.setValue(play);
    }

    public ProbePlanerData getProbePlanerData() {
        return probePlanerData.getValue();
    }

    public Property<ProbePlanerData> probePlanerDataProperty() {
        return probePlanerData;
    }

    public void setFile(File file) {
        this.file.setValue(file);
    }

}
