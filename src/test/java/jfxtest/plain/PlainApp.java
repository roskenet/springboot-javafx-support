package jfxtest.plain;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;

@SpringBootApplication
public class PlainApp extends AbstractJavaFxApplicationSupport{

    public static void main(String[] args) {
            launchApp(PlainApp.class, PlainView.class, args);
    }
}