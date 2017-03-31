package de.felixroske.jfxsupport;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

public class PropertyArrayReaderTest {

    Environment envArrayMock = Mockito.mock(Environment.class);
    Environment envSingleEntryMock = Mockito.mock(Environment.class);
   
    @Before
    public void setUp() {
        // This is what Spring environment returns
        // When we defined an array in of appicons: ('- entry_1' ... in the yaml):
        when(envArrayMock.getProperty("javafx.appicons")).thenReturn(null);
        when(envArrayMock.getProperty("javafx.appicons[0]")).thenReturn("entry_0");
        when(envArrayMock.getProperty("javafx.appicons[1]")).thenReturn("entry_1");
        when(envArrayMock.getProperty("javafx.appicons[2]")).thenReturn("entry_2");
        
        // When there is a single entry:
        when(envSingleEntryMock.getProperty("javafx.appicons")).thenReturn("entry");
        when(envSingleEntryMock.getProperty("javafx.appicons[0]")).thenReturn(null);
    }
   
    @Test
    public void testForSingleValue() throws Exception {
       List<String> list = PropertyArrayReader.get(envSingleEntryMock, "javafx.appicons");
       assertThat(list, IsIterableContainingInAnyOrder.containsInAnyOrder("entry"));
    } 
    
    @Test
    public void testForMultipleValues() {
        List<String> list = PropertyArrayReader.get(envArrayMock, "javafx.appicons");
        assertThat(list, IsIterableContainingInAnyOrder
                .containsInAnyOrder("entry_0", "entry_1", "entry_2"));
    }

}
