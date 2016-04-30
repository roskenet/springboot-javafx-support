package de.felixroske.killerapp.buttons;

import de.felixroske.jfx.support.FXMLController;

@FXMLController
public class ButtonsController  {

    public void topButtonClicked() {
        System.out.println("Du hast den topButton geklickt!");
    }
    
    public void clickMiddleButton() {
        System.out.println("MiddleButton!");
    }
}
