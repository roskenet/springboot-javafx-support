package de.felixroske.jfxtest;

import de.felixroske.jfxsupport.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.beans.*;
import org.springframework.boot.test.context.*;
import org.springframework.context.*;
import org.springframework.test.context.junit.jupiter.*;
import org.testfx.framework.junit5.*;

import javafx.scene.*;
import javafx.stage.*;

@SpringBootTest (classes = SampleConfig.class)
@ExtendWith ({SpringExtension.class, ApplicationExtension.class})
public class SpringJavaFxTestingBase implements ApplicationContextAware {

    protected ApplicationContext applicationContext;
    private AbstractFxmlView controllerViewBean;

    @BeforeAll
    public static void beforeAll() {
        String headlessProp = System.getProperty("JAVAFX_HEADLESS", "true");
        Boolean headless = Boolean.valueOf(headlessProp);
        String geometryProp = System.getProperty("JAVAFX_GEOMETRY", "1600x1200-32");

        if (headless) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("headless.geometry", geometryProp);
        }
        else {
            System.setProperty("java.awt.headless", "false");
        }
    }

    protected void init(AbstractFxmlView viewBean) {
        this.controllerViewBean = viewBean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Start
    public void start(Stage stage) throws Exception {
        if (controllerViewBean == null)
            throw new Exception("The view is null! Have you called init() before?");
        Scene viewScene = controllerViewBean.getView().getScene();
        if (viewScene == null) {
            Scene newScene = new Scene(controllerViewBean.getView());
            stage.setScene(newScene);
        }
        else
            stage.setScene(viewScene);
        stage.show();
        stage.centerOnScreen();
        stage.toFront();
    }
}