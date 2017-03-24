package jfxtest.annotated;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.roskenet.jfxsupport.test.GuiTest;
import javafx.scene.control.Button;
@RunWith(SpringRunner.class)
@SpringBootTest
public class AnnotatedViewTest extends GuiTest {
	
	@Autowired
	private AnnotatedView buttonsView;
	
    @PostConstruct
    public void constructView() throws Exception {
        init(buttonsView);
    }
	
	@Test
	// Should start up finding all resources without throwing exceptions
	public void appStartsUp() throws Exception {
		assertThat(buttonsView, isA(AbstractFxmlView.class));
	}
	
	@Test
	public void showsI18nText() throws Exception {
	   Button theButton = (Button) find("#theButton");
	   
	   assertThat(theButton.getText(), is("The Button Text"));
	}
}
