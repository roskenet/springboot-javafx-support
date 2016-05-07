package de.felixroske.killerapp.main;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

@FXMLController
public class MainPresenterImpl implements Initializable, MainPresenter{

	@Autowired
	private MainModel mainModel;
	
	@FXML
	private CheckBox active;

	@FXML
	private Button clickButton;

	@FXML
	private TextField someText;

	@Override
	public void onButtonClicked(ActionEvent e) {
		System.out.println("Button wurde geclickt!");
	}

	@Override
	public void onLabelClicked(MouseEvent e) {
		System.out.println("Das Label wurde angeclickt!");
		mainModel.doSomething();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		clickButton.disableProperty().bind(active.selectedProperty());
		someText.textProperty().bindBidirectional(mainModel.theFieldProperty());
	}

}
