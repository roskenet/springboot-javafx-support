package de.felixroske.jfxsupport;

import de.felixroske.jfxtest.*;
import org.hamcrest.*;
import org.junit.jupiter.api.*;
import org.testfx.api.*;

import java.util.*;
import javafx.scene.image.*;

import static org.junit.Assert.*;

public class AbstractJavaFxApplicationSupportTest {

    private AbstractJavaFxApplicationSupport app;

    public class TestApp extends AbstractJavaFxApplicationSupport {

    }

    @BeforeAll
    public static void beforeClass() {
        System.setProperty("testfx.headless", "true");
    }

    @AfterAll
    public static void afterClass() {
        System.setProperty("testfx.headless", "false");
    }

    @BeforeEach
    public void setup() throws Exception {
        FxToolkit.registerPrimaryStage();
        app = new TestApp();
        app.savedInitialView = SampleView.class;
        app.splashScreen = new SplashScreen();
        FxToolkit.setupApplication(() -> app);
    }

    @Test
    @DisplayName ("Load default icons")
    public void loadDefaultIcons() {
        final Collection<Image> images = new ArrayList<>();
        images.addAll(app.loadDefaultIcons());
        assertThat(images.size(), CoreMatchers.is(5));
    }
}