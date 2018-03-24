package de.felixroske.jfxsupport;

import de.felixroske.jfxtest.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.testfx.framework.junit5.*;
import org.testfx.util.*;

import java.lang.management.*;
import javafx.application.*;
import javafx.stage.*;

import static org.testfx.api.FxAssert.*;
import static org.testfx.matcher.base.NodeMatchers.*;

public class SampleViewTest extends SpringJavaFxTestingBase {

    @Autowired
    SampleView view;

    Stage stage;

    @Start
    public void start(Stage stage) throws Exception {
        this.stage = stage;
    }

    @AfterEach
    void afterEach() {
        Platform.runLater(() -> view.hide());
    }

    @Test
    @DisplayName ("Show view")
    void showViewTest() {
        Platform.runLater(() -> view.showView(Modality.APPLICATION_MODAL));
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(view.getView(), isVisible());
    }

    @Test
    @DisplayName ("Show view given stage")
    void showViewGivenStageTest() {
        Platform.runLater(() -> view.showView(stage, Modality.NONE));
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(view.getView(), isVisible());
    }

    @Test
    @DisplayName ("Show view and wait")
    void showViewAndWaitTest() {
        Platform.runLater(() -> view.showViewAndWait(Modality.WINDOW_MODAL));
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(view.getView(), isVisible());
    }

    @Test
    @DisplayName ("Show view and wait given stage")
    void showViewAndWaitGivenStageTest() {
        Platform.runLater(() -> view.showViewAndWait(stage, Modality.APPLICATION_MODAL));
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(view.getView(), isVisible());
    }
}