package de.felixroske.jfxsupport;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class SplashScreenTest {

    class TestSplashScreen extends SplashScreen {
        @Override
        public Parent getParent() {
            Pane pane = new Pane();
            return pane;
        }
    }

    @Test
    public void testGetParent() {
        SplashScreen splashScreen = new TestSplashScreen();
        Parent parent = splashScreen.getParent();
        assertThat(parent, is(instanceOf(Pane.class)));
    }

    @Test
    public void testVisible() {
        SplashScreen splashScreen = new TestSplashScreen();
        assertThat(splashScreen.visible(), is(Boolean.TRUE));
    }

    @Test
    public void testGetImagePath() {
        SplashScreen splashScreen = new TestSplashScreen();
        assertThat(splashScreen.getImagePath(), is("/splash/javafx.png"));
    }

}
