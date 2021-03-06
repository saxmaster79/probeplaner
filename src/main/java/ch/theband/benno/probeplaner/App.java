package ch.theband.benno.probeplaner;

import ch.theband.benno.probeplaner.frame.FrameView;
import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Benno
 */
public class App extends Application {

    @Override
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        Injector.setLogger(System.out::println);
    }

    @Override
    public void start(Stage stage) {
        FrameView frameView = new FrameView();
        Scene scene = new Scene(frameView.getView());
        stage.setTitle("Probeplaner - neu");
        stage.setWidth(1400);
        final String uri = getClass().getResource("app.css").toExternalForm();
        scene.getStylesheets().add(uri);
        stage.setScene(scene);
        stage.show();
        ProbePlanerModel model = (ProbePlanerModel) Injector.instantiateModelOrService(ProbePlanerModel.class);
        model.savedFileProperty().addListener((p, o, n) -> stage.setTitle("Probeplaner - " + n.getName()));
    }

    @Override
    public void stop() {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        System.out.println("java.version " +System.getProperty("java.version"));
        launch(args);
    }
}
