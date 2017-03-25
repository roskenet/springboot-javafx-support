package jfxtest.loader;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;

@SpringBootApplication
public class AppLoaderTest extends AbstractJavaFxApplicationSupport{
    
    @Test
    public void appStartsUp() throws Exception {
       launchApp(AppLoaderTest.class, AppLoaderView.class, new String[]{""});
    }
}
