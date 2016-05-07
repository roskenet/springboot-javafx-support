package de.felixroske.jfxtest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ButtonViewTest.class})
public class ButtonViewTest {

	@Test
	public void writeMe() throws Exception {
		Assert.assertTrue(true);
	}
}