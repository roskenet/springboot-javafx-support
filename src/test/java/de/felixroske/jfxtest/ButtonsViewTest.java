package de.felixroske.jfxtest;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import de.saxsys.mvvmfx.testingutils.jfxrunner.JfxRunner;
import de.saxsys.mvvmfx.testingutils.jfxrunner.TestInJfxThread;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import de.felixroske.jfxsupport.AbstractFxmlView;
import java.lang.reflect.Method;
import static org.hamcrest.CoreMatchers.is;
/**
 * TEST ME!
 * 
 * @author felix
 *
 */
@RunWith(JfxRunner.class)
@ContextConfiguration(classes = {ButtonsController.class, ButtonsView.class})
public class ButtonsViewTest {
	
	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public SpringMethodRule springMethodRule = new SpringMethodRule();

	@Autowired
	private ButtonsView buttonsView;
	
	@Autowired
	private ButtonsController buttonsController;
	
	@Test
	public void writeMe() throws Exception {
		assertThat(buttonsView, isA(AbstractFxmlView.class));
		
		buttonsController.topButtonClicked();
	}

	@Test
	public void fxmlPathIsInPackage() throws Exception {
		final Method getFxmlPath = AbstractFxmlView.class.getDeclaredMethod("getFxmlPath");
		getFxmlPath.setAccessible(true);
		assertThat(getFxmlPath.invoke(buttonsView), is("/de/felixroske/jfxtest/buttons.fxml"));
	}

	@Test
	@TestInJfxThread
	public void fxmlFileIsLoaded() throws Exception {
		final Parent view = buttonsView.getView();
		assertThat(view instanceof Pane, is(true));
	}
}
