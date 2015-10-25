package ch.theband.benno.probeplaner;

import com.airhacks.afterburner.injection.Injector;

import ch.theband.benno.probeplaner.frame.FrameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author adam-bien.com
 */
public class App extends Application {

	@Override
	public void init() throws Exception {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
	}

	@Override
	public void start(Stage stage) throws Exception {
		FrameView frameView = new FrameView();
		Scene scene = new Scene(frameView.getView());
		stage.setTitle("Probeplaner");
		stage.setWidth(1400);
		final String uri = getClass().getResource("app.css").toExternalForm();
		scene.getStylesheets().add(uri);
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		Injector.forgetAll();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
