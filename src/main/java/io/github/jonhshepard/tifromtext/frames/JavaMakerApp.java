package io.github.jonhshepard.tifromtext.frames;

import io.github.jonhshepard.tifromtext.frames.stages.MainStage;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author JonhSHEPARD
 */
public class JavaMakerApp extends Application {

    private Stage currentStage = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        if(currentStage != null) currentStage.close();

        currentStage = new MainStage();
        currentStage.show();
        currentStage.sizeToScene();
		currentStage.getScene().getStylesheets().add("bootstrapfx.css");
    }
}
