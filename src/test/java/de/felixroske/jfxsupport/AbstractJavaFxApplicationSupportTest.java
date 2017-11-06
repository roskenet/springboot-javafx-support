package de.felixroske.jfxsupport;

import javafx.scene.image.Image;
import jfxtest.annotated.AnnotatedView;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertThat;

/**
 * Created on 11/3/2017 for Onyx.
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class AbstractJavaFxApplicationSupportTest { //extends GuiTest {

    private AbstractJavaFxApplicationSupport app;

    public class TestApp extends AbstractJavaFxApplicationSupport {

    }

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("testfx.headless", "true");
    }

    @AfterClass
    public static void afterClass() {
        System.setProperty("testfx.headless", "false");
    }

    @Before
    public void setup() throws Exception {
        FxToolkit.registerPrimaryStage();
        app = new TestApp();
        app.savedInitialView = AnnotatedView.class;
        app.splashScreen = new SplashScreen();
        FxToolkit.setupApplication(() -> app);
    }

    @Test
    public void loadDefaultIcons() throws Exception {
        final Collection<Image> images = new ArrayList<>();
        images.addAll(app.loadDefaultIcons());
        assertThat(images.size(), CoreMatchers.is(5));
    }

}