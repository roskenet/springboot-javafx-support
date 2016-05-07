package de.felixroske.jfxtest;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.felixroske.jfxsupport.AbstractFxmlView;
/**
 * TEST ME!
 * 
 * @author felix
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ButtonsController.class, ButtonsView.class})
public class ButtonsViewTest {
	
	@Autowired
	private ButtonsView buttonsView;
	
	@Autowired
	private ButtonsController buttonsController;
	
	@Test
	public void writeMe() throws Exception {
		assertThat(buttonsView, isA(AbstractFxmlView.class));
		
		buttonsController.topButtonClicked();
	}
}
