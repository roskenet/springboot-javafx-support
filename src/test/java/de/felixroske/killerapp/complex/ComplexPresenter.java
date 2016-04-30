package de.felixroske.killerapp.complex;

import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfx.support.FXMLController;
import de.felixroske.killerapp.buttons.ButtonsView;
import de.felixroske.killerapp.list.SomeListView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;

@FXMLController
public class ComplexPresenter {

    @Autowired
    private ButtonsView buttonsView;
   
    @Autowired
    private SomeListView someListView;
    
    @FXML
    private ScrollPane scrollPane;
    
    public void aboutMenuItem() {
        System.out.println("Du hast etwas ausgewaehlt!");
        scrollPane.setContent(buttonsView.getView());
    }

    public void showSomeList() {
        scrollPane.setContent(someListView.getView());
    }

    public void showAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText("I have a great message for you!");

        alert.showAndWait();
    }
}
