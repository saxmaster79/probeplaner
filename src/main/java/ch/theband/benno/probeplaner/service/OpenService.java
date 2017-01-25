package ch.theband.benno.probeplaner.service;

import ch.theband.benno.probeplaner.model.Play;
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
import java.util.ArrayList;

public class OpenService extends ErrorHandlingService<ProbePlanerData> {
    private final Property<File> file = new SimpleObjectProperty<>();

    @Override
    protected Task<ProbePlanerData> createTask() {
        return new Task<ProbePlanerData>() {

            @Override
            protected ProbePlanerData call() throws IOException, ClassNotFoundException {
                Path path = file.getValue().toPath();
                System.out.println("Reading file " + path);
                String xml = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                XStream xstream = new XStream(new StaxDriver());
                Object result = xstream.fromXML(xml);
                final ProbePlanerData data;
                if (result instanceof Play) {
                    data = new ProbePlanerData((Play) result, new ArrayList<>());
                } else {
                    data = (ProbePlanerData) result;
                }
                return data;
            }

        };
    }

    public void setFile(File file) {
        this.file.setValue(file);
    }

}
