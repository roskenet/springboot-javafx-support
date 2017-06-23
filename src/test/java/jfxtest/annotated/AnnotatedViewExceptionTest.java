package jfxtest.annotated;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AnnotatedViewExceptionTest {
	
	@Autowired
	private AnnotatedExceptionView buttonsView;
	
	@Test(expected = IllegalStateException.class)
	public void appStartsUp() throws Exception {
	    buttonsView.getView();
		
	}

}
