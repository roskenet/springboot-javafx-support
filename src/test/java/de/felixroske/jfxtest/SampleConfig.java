package de.felixroske.jfxtest;

import org.springframework.context.annotation.*;

@Configuration
public class SampleConfig {

    @Bean
    public SampleView testView() {
        return new SampleView();
    }

    @Bean
    public SampleIncorrectView erroredView() {
        return new SampleIncorrectView();
    }
}