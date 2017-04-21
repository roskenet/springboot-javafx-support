package de.felixroske.jfxsupport;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The enum {@link GUIState} stores Scene and Stage objects as singletons in
 * this VM.
 *
 * @author Felix Roske
 * @author Andreas Jay
 */
public enum GUIState {

	INSTANCE;
	private static Scene scene;

	private static Stage stage;

	private static String title;

	public static String getTitle() {
		return title;
	}

	public static Scene getScene() {
		return scene;
	}

	public static Stage getStage() {
		return stage;
	}

	public static void setScene(final Scene scene) {
		GUIState.scene = scene;
	}

	public static void setStage(final Stage stage) {
		GUIState.stage = stage;
	}

	public static void setTitle(final String title) {
		GUIState.title = title;
	}

}
