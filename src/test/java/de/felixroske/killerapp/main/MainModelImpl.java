package de.felixroske.killerapp.main;

import org.springframework.stereotype.Component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Component
public class MainModelImpl implements MainModel {

	private StringProperty theField = new SimpleStringProperty();

	public void setTheField(String theField) {
		this.theField.set(theField);
	}
	
	public String getTheField() {
		return theField.get();
	}
	
	@Override
	public StringProperty theFieldProperty() {
		return theField;
	}
	
	@Override
	public void doSomething() {
		// Some magic happens here!
		String newString = getTheField().toUpperCase();
		setTheField(newString);
	}
}
