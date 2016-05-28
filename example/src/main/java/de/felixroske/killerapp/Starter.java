package de.felixroske.killerapp;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.killerapp.complex.ComplexView;

@Lazy
@SpringBootApplication
public class Starter extends AbstractJavaFxApplicationSupport {
	
	public static void main(String[] args) {
		launchApp(Starter.class, ComplexView.class, args);
	}
	
}
