package net.springjfx.test.plain;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.springjfx.AbstractJavaFxApplicationSupport;

@SpringBootApplication
public class PlainApp extends AbstractJavaFxApplicationSupport{

    public static void main(String[] args) {
            launchApp(PlainApp.class, PlainView.class, args);
    }
}