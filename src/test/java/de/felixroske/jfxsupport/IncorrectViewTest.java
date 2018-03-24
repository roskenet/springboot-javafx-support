package de.felixroske.jfxsupport;

import de.felixroske.jfxtest.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.testfx.framework.junit5.*;

import javafx.stage.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class IncorrectViewTest extends SpringJavaFxTestingBase {

    @Autowired
    SampleIncorrectView incorrectView;
    Stage stage;

    @Start
    public void start(Stage stage) throws Exception {
        this.stage = stage;
    }

    @Test
    @DisplayName ("View with incorrect location")
    void viewWithIncorrectLocationTest() {
        init(incorrectView);
        assertThrows(IllegalStateException.class, () -> super.start(stage));
    }
}