package de.felixroske.jfxsupport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.core.env.Environment;

/**
 * The utility PropertyReaderHelper.
 *
 * @author Felix Roske
 * @author Andreas Jay
 */
public class PropertyReaderHelper {

	private PropertyReaderHelper() {
	}

	/**
	 * Lookup in {@link Environment} a certain property or a list of properties.
	 *
	 * @param env
	 *            the {@link Environment} context from which to
	 * @param propName
	 *            the name of the property to lookup from {@link Environment}.
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
	 * Load from {@link Environment} a key with a given type. If sucj key is
	 * present supply it in {@link Consumer}.
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

	/**
	 * Determine file path from package name creates from class package instance
	 * the file path equivalent. The path will be prefixed and suffixed with a
	 * slash.
	 *
	 * @return the path equivalent to a package structure.
	 */
	public static final String determineFilePathFromPackageName(final Class<?> clazz) {
		return "/" + clazz.getPackage().getName().replace('.', '/') + "/";
	}
}
