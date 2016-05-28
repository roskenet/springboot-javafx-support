package de.felixroske.killerapp.buttons;

import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.killerapp.Starter;

@FXMLController
public class ButtonsController  {

	@Autowired
	Starter starter;
	
    public void topButtonClicked() {
        System.out.println("Du hast den topButton geklickt!");
    }
    
    public void clickMiddleButton() {
        System.out.println("MiddleButton!");
        System.out.println("ApplicationHash: " + starter.hashCode());
    }
}
