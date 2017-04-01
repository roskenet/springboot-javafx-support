package de.felixroske.jfxsupport;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

public class PropertyReaderHelperTest {

    Environment envArrayMock = Mockito.mock(Environment.class);
    Environment envSingleEntryMock = Mockito.mock(Environment.class);
   
    @Before
    public void setUp() {
        // This is what Spring environment returns
        // When we defined an array in of appicons: ('- entry_1' ... in the yaml):
        when(envArrayMock.getProperty("entry")).thenReturn(null);
        when(envArrayMock.getProperty("entry[0]")).thenReturn("entry_0");
        when(envArrayMock.getProperty("entry[1]")).thenReturn("entry_1");
        when(envArrayMock.getProperty("entry[2]")).thenReturn("entry_2");
        
        // When there is a single entry:
        when(envSingleEntryMock.getProperty("entry")).thenReturn("entry");
        when(envSingleEntryMock.getProperty("entry[0]")).thenReturn(null);
    }
   
    @Test
    public void testGet_SingleValue() throws Exception {
       List<String> list = PropertyReaderHelper.get(envSingleEntryMock, "entry");
       assertThat(list, IsIterableContainingInAnyOrder.containsInAnyOrder("entry"));
    } 
    
    @Test
    public void testGet_MultipleValues() {
        List<String> list = PropertyReaderHelper.get(envArrayMock, "entry");
        assertThat(list, IsIterableContainingInAnyOrder
                .containsInAnyOrder("entry_0", "entry_1", "entry_2"));
    }

    @Test
    public void testSetIfPresent_ExistingKey() throws Exception {
       TestObject testObject = new TestObject();
       
       PropertyReaderHelper.setIfPresent(envSingleEntryMock, "entry", String.class, testObject::setEntry);
        
       assertThat(testObject.getEntry(), is("entry"));
    }
    
    @Test
    public void testSetIfPresent_NonExistingKey() throws Exception {

       TestObject testObject = new TestObject();
       
       PropertyReaderHelper.setIfPresent(envSingleEntryMock, "no_entry", String.class, testObject::setEntry);
        
       assertThat(testObject.getEntry(), is("UNSET"));
    }
    
    class TestObject {
        private String entry = "UNSET";
        public void setEntry(String theEntryValue) {
            this.entry = theEntryValue;
        }
        public String getEntry() {
            return entry;
        }
    }
    
}
