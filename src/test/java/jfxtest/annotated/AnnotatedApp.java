package jfxtest.annotated;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;

@SpringBootApplication
public class AnnotatedApp extends AbstractJavaFxApplicationSupport{

    public static void main(String[] args) {
            launchApp(AnnotatedApp.class, AnnotatedView.class, args);
    }
}