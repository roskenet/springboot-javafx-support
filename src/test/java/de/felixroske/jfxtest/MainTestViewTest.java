package de.felixroske.jfxtest;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MainTestViewTest.class)
@Configuration
public class MainTestViewTest {
    
    @Bean
    public MainTestView mainTestView() {
        return new MainTestView();
    }

    @Autowired
    private MainTestView mainTestView;
    
    @Test
    public void testAutowired() throws Exception {
       assertThat(mainTestView, isA(MainTestView.class)); 
    }
    
//    @Test
//    public void testControllerBean() throws Exception {
//        Object presenter = buttonsView.getPresenter();
//        assertThat(presenter, isA(ButtonsController.class)); 
//    }
}
