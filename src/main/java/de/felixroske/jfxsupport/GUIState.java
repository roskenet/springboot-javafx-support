package de.felixroske.jfxsupport;

import javafx.scene.Scene;
import javafx.stage.Stage;

public final class GUIState {
    
    // The GUI has one and only one state
    private GUIState() {
    }
    
    private static Stage stage;
    private static Scene scene;

    public static synchronized Stage getStage() {
        return stage;
    }
    public static synchronized void setStage(Stage stage) {
        GUIState.stage = stage;
    }
    public static synchronized Scene getScene() {
        return scene;
    }
    public static synchronized void setScene(Scene scene) {
        GUIState.scene = scene;
    }

}
