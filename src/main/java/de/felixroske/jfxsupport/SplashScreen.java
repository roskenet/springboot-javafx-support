package de.felixroske.jfxsupport;

import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/*
 * A default standard splash pane implementation Subclass it and override it's methods to customize with your own
 * behaviour.
 */
public class SplashScreen {
    private static String DEFAULT_IMAGE = "/splash/javafx.png";

    /**
     * Override this to create your own splash pane parent node
     * 
     * @return A standard image
     */
    public Parent getParent() {
        Pane imagePane = new Pane();
        Image img = new Image(getClass().getResource(getImagePath()).toExternalForm());
        imagePane.getChildren().add(new ImageView(img));
        return imagePane;
    }

    /**
     * Customize if the splash screen should be visible at all
     * 
     * @return true by default
     */
    public boolean visible() {
        return true;
    }
    /**
     * Use your own splash image instead of the default one
     * @return "/splash/javafx.png"
     */
    public String getImagePath() {
        return DEFAULT_IMAGE;
    }

}
