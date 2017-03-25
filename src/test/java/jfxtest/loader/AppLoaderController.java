package jfxtest.loader;

import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

@FXMLController
public class AppLoaderController {
    
    @Autowired
    private AppLoaderView theView;
    
    public void initialize() throws Exception {
//        theView.getView().visibleProperty().addListener((a, b, c) -> {
//            System.out.println("Hallo");
//        });
        Platform.exit();
    }

}
