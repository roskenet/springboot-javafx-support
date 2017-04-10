package playground;

import static org.junit.Assert.*;

import org.junit.Test;

public class JavaTest {

    @Test
    public void test() {
       
        String myString = "string";
        String nullString = null;
        
        assertTrue("string".equals(myString));
        assertFalse("string".equals(nullString));
    }

}
