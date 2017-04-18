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

	/** The instance. */
	INSTANCE;
	/** The scene. */
	private static Scene scene;

	/** The stage. */
	private static Stage stage;

	/** The title. */
	private static String title;

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public static String getTitle() {
		return title;
	}

	/**
	 * Gets the scene.
	 *
	 * @return the scene
	 */
	public static Scene getScene() {
		return scene;
	}

	/**
	 * Gets the stage.
	 *
	 * @return the stage
	 */
	public static Stage getStage() {
		return stage;
	}

	/**
	 * Sets the scene.
	 *
	 * @param scene
	 *            the new scene
	 */
	public static void setScene(final Scene scene) {
		GUIState.scene = scene;
	}

	/**
	 * Sets the stage.
	 *
	 * @param stage
	 *            the new stage
	 */
	public static void setStage(final Stage stage) {
		GUIState.stage = stage;
	}

	/**
	 * Sets the title.
	 *
	 * @param title
	 *            the new title
	 */
	public static void setTitle(final String title) {
		GUIState.title = title;
	}

}
