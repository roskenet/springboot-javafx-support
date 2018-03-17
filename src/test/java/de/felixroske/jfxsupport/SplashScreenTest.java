package de.felixroske.jfxsupport;

import de.felixroske.jfxtest.*;
import org.junit.jupiter.api.*;

import javafx.scene.*;
import javafx.scene.layout.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class SplashScreenTest extends SpringJavaFxTestingBase {

    SplashScreen splashScreen;

    @BeforeEach
    public void beforeEach() {
        splashScreen = new SplashScreen();
    }

    @Test
    @DisplayName ("Get parent")
    public void getParentTest() {
        Parent parent = splashScreen.getParent();
        assertThat(parent, is(instanceOf(Pane.class)));
    }

    @Test
    @DisplayName ("Is visible test")
    public void isVisibleTest() {
        assertThat(splashScreen.visible(), is(Boolean.TRUE));
    }

    @Test
    @DisplayName ("Get image path")
    public void getImagePathTest() {
        assertThat(splashScreen.getImagePath(), is("/splash/javafx.png"));
    }
}
