package net.springjfx.test.plain;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import javax.annotation.PostConstruct;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.springjfx.AbstractFxmlView;
import net.springjfx.test.GuiTest;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class PlainViewTest extends GuiTest {

	@Autowired
	private PlainView buttonsView;

	@PostConstruct
	public void constructView() throws Exception {
		init(buttonsView);
	}

	@Test
	public void appStartsUp() throws Exception {
		assertThat(buttonsView, isA(AbstractFxmlView.class));
	}
}
