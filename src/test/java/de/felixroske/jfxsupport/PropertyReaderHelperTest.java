package de.felixroske.jfxsupport;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

public class PropertyReaderHelperTest {

	private Environment envArrayMock;
	private Environment envSingleEntryMock;

	@Before
	public void setUp() {
		envArrayMock = Mockito.mock(Environment.class);
		envSingleEntryMock = Mockito.mock(Environment.class);
		// This is what Spring environment returns
		// When we defined an array in of appicons: ('- entry_1' ... in the
		// yaml):
		when(envArrayMock.getProperty("entry")).thenReturn(null);
		when(envArrayMock.getProperty("entry[0]")).thenReturn("entry_0");
		when(envArrayMock.getProperty("entry[1]")).thenReturn("entry_1");
		when(envArrayMock.getProperty("entry[2]")).thenReturn("entry_2");

		// When there is a single entry:
		when(envSingleEntryMock.getProperty("entry")).thenReturn("entry");
		when(envSingleEntryMock.getProperty("entry[0]")).thenReturn(null);
		when(envSingleEntryMock.getProperty(eq("stringentry"), eq(String.class))).thenReturn("entry");

	}

	@Test
	public void testGet_SingleValue() throws Exception {
		final List<String> list = PropertyReaderHelper.get(envSingleEntryMock, "entry");
		assertThat(list, IsIterableContainingInAnyOrder.containsInAnyOrder("entry"));
	}

	@Test
	public void testGet_MultipleValues() {
		final List<String> list = PropertyReaderHelper.get(envArrayMock, "entry");
		assertThat(list, IsIterableContainingInAnyOrder.containsInAnyOrder("entry_0", "entry_1", "entry_2"));
	}

	@Test
	public void testSetIfPresent_ExistingKey() throws Exception {
		final TestObject testObject = new TestObject();

		PropertyReaderHelper.setIfPresent(envSingleEntryMock, "stringentry", String.class, testObject::setStringEntry);

		assertThat(testObject.getStringEntry(), is("entry"));
	}

	@Test
	public void testSetIfPresent_NonExistingKey() throws Exception {
		final TestObject testObject = new TestObject();

		PropertyReaderHelper.setIfPresent(envSingleEntryMock, "no_entry", String.class, testObject::setStringEntry);

		assertThat(testObject.getStringEntry(), is("UNSET"));
	}

	class TestObject {
		private String stringEntry = "UNSET";
		private Long longEntry = Long.valueOf(0);

		public String getStringEntry() {
			return stringEntry;
		}

		public void setStringEntry(final String theEntryValue) {
			stringEntry = theEntryValue;
		}

		public Long getLongEntry() {
			return longEntry;
		}

		public void setLongEntry(final Long longEntry) {
			this.longEntry = longEntry;
		}
	}

	@Test
	public void testDetermineFilePathFromPackageName() {
		final String path = PropertyReaderHelper.determineFilePathFromPackageName(getClass());
		assertEquals("/de/felixroske/jfxsupport/", path);
	}

}
