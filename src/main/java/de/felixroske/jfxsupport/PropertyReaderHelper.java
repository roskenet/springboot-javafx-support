package de.felixroske.jfxsupport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.core.env.Environment;

/**
 * The Class PropertyReaderHelper.
 */
public class PropertyReaderHelper {

	/**
	 * Gets the.
	 *
	 * @param env
	 *            the env
	 * @param propName
	 *            the prop name
	 * @return the list
	 */
	public static List<String> get(final Environment env, final String propName) {
		final List<String> list = new ArrayList<>();

		final String singleProp = env.getProperty(propName);
		if (singleProp != null) {
			list.add(singleProp);
			return list;
		}

		int counter = 0;
		String prop = env.getProperty(propName + "[" + counter + "]");
		while (prop != null) {
			list.add(prop);
			counter++;
			prop = env.getProperty(propName + "[" + counter + "]");
		}

		return list;
	}

	/**
	 * Sets the if present.
	 *
	 * @param <T>
	 *            the generic type
	 * @param env
	 *            the env
	 * @param key
	 *            the key
	 * @param type
	 *            the type
	 * @param function
	 *            the function
	 */
	public static <T> void setIfPresent(final Environment env, final String key, final Class<T> type,
			final Consumer<T> function) {
		final T value = env.getProperty(key, type);
		if (value != null) {
			function.accept(value);
		}
	}
}
