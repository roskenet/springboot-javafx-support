package de.felixroske.jfxsupport;

import static java.util.ResourceBundle.getBundle;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

/**
 * This class is derived from Adam Bien's
 * <a href="http://afterburner.adam-bien.com/">afterburner.fx</a> project.
 * <p>
 * {@link AbstractFxmlView} is a stripped down version of <a href=
 * "https://github.com/AdamBien/afterburner.fx/blob/02f25fdde9629fcce50ea8ace5dec4f802958c8d/src/main/java/com/airhacks/afterburner/views/FXMLView.java"
 * >FXMLView</a> that provides DI for Java FX Controllers via Spring.
 * 
 * Felix Roske (felix.roske@zalando.de) changed this to use annotation for fxml path.
 * 
 * @author Thomas Darimont
 */
public abstract class AbstractFxmlView implements ApplicationContextAware {

	protected ObjectProperty<Object> presenterProperty;
	protected FXMLLoader fxmlLoader;
	protected ResourceBundle bundle;

	protected URL resource;

	private ApplicationContext applicationContext;
	private String fxmlRoot;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if (this.applicationContext != null) {
			return;
		}

		this.applicationContext = applicationContext;
	}

	public AbstractFxmlView() {
	    // Set the root path to package path
        setFxmlRootPath("/" + getClass().getPackage().getName().replace('.', '/') + "/");

        // TODO refactor me!
        FXMLView annotation = getFXMLAnnotation();
        if (annotation != null && !annotation.value().equals("")) {
            this.resource = getClass().getResource(annotation.value());
        } else {
            this.resource = getClass().getResource(getFxmlPath());
        }

        this.presenterProperty = new SimpleObjectProperty<>();
        this.bundle = getResourceBundle(getBundleName());
	}

	private FXMLView getFXMLAnnotation() {
		Class<? extends AbstractFxmlView> theClass = this.getClass();
		FXMLView annotation = theClass.getAnnotation(FXMLView.class);
		return annotation;
	}

	private Object createControllerForType(Class<?> type) {
		return this.applicationContext.getBean(type);
	}

	private void setFxmlRootPath(String path) {
		if (path.endsWith("/")) {
			this.fxmlRoot = path;
		} else {
			this.fxmlRoot = path + "/";
		}
	}

	FXMLLoader loadSynchronously(URL resource, ResourceBundle bundle) throws IllegalStateException {

		FXMLLoader loader = new FXMLLoader(resource, bundle);
		loader.setControllerFactory(this::createControllerForType);

		try {
			loader.load();
		} catch (IOException ex) {
			throw new IllegalStateException("Cannot load " + getConventionalName(), ex);
		}

		return loader;
	}

	void ensureFxmlLoaderInitialized() {

		if (this.fxmlLoader != null) {
			return;
		}

		this.fxmlLoader = loadSynchronously(resource, bundle);
		this.presenterProperty.set(this.fxmlLoader.getController());
	}

	/**
	 * Initializes the view by loading the FXML (if not happened yet) and
	 * returns the top Node (parent) specified in the FXML file.
	 *
	 * @return
	 */
	public Parent getView() {

		ensureFxmlLoaderInitialized();

		Parent parent = fxmlLoader.getRoot();
		addCSSIfAvailable(parent);
		return parent;
	}

	/**
	 * Initializes the view synchronously and invokes the consumer with the
	 * created parent Node within the FX UI thread.
	 *
	 * @param consumer
	 *            - an object interested in received the {@link Parent} as
	 *            callback
	 */
	public void getView(Consumer<Parent> consumer) {
		CompletableFuture.supplyAsync(this::getView, Platform::runLater).thenAccept(consumer);
	}

	/**
	 * Scene Builder creates for each FXML document a root container. This
	 * method omits the root container (e.g. {@link AnchorPane}) and gives you
	 * the access to its first child.
	 *
	 * @return the first child of the {@link AnchorPane}
	 */
	public Node getViewWithoutRootContainer() {

		ObservableList<Node> children = getView().getChildrenUnmodifiable();
		if (children.isEmpty()) {
			return null;
		}

		return children.listIterator().next();
	}

	void addCSSIfAvailable(Parent parent) {
		
	    // Read global css when available:
	    List<String> list = PropertyReaderHelper.get(applicationContext.getEnvironment(), "javafx.css");
	    if(!list.isEmpty()) {
	        list.forEach(css -> 
	            parent.getStylesheets().add(getClass().getResource(css).toExternalForm()));
	    }
	    
		// TODO refactor me!
		FXMLView annotation = getFXMLAnnotation();
		if(annotation != null && annotation.css().length > 0) {
			for (String cssFile : annotation.css()) {
				URL uri = getClass().getResource(cssFile);
				String uriToCss = uri.toExternalForm();
				parent.getStylesheets().add(uriToCss);
			}
			return;
		}
		
		URL uri = getClass().getResource(getStyleSheetName());
		if (uri == null) {
			return;
		}

		String uriToCss = uri.toExternalForm();
		parent.getStylesheets().add(uriToCss);
	}

	private String getStyleSheetName() {
		return fxmlRoot + getConventionalName(".css");
	}

	/**
	 * In case the view was not initialized yet, the conventional fxml
	 * (airhacks.fxml for the AirhacksView and AirhacksPresenter) are loaded and
	 * the specified presenter / controller is going to be constructed and
	 * returned.
	 *
	 * @return the corresponding controller / presenter (usually for a
	 *         AirhacksView the AirhacksPresenter)
	 */
	public Object getPresenter() {

		ensureFxmlLoaderInitialized();

		return this.presenterProperty.get();
	}

	/**
	 * Does not initialize the view. Only registers the Consumer and waits until
	 * the the view is going to be created / the method FXMLView#getView or
	 * FXMLView#getViewAsync invoked.
	 *
	 * @param presenterConsumer
	 *            listener for the presenter construction
	 */
	public void getPresenter(Consumer<Object> presenterConsumer) {

		this.presenterProperty.addListener((ObservableValue<? extends Object> o, Object oldValue, Object newValue) -> {
			presenterConsumer.accept(newValue);
		});
	}

	/**
	 * @param ending
	 *            the suffix to append
	 * @return the conventional name with stripped ending
	 */
	protected String getConventionalName(String ending) {
		return getConventionalName() + ending;
	}

	/**
	 * @return the name of the view without the "View" prefix in lowerCase. For
	 *         AirhacksView just airhacks is going to be returned.
	 */
	protected String getConventionalName() {
		return stripEnding(getClass().getSimpleName().toLowerCase());
	}

	String getBundleName() {
	    // TODO refactor me!
        FXMLView annotation = getFXMLAnnotation();
        if (annotation != null && !"".equals(annotation.bundle())) {
           return annotation.bundle();
        } else {
            return getClass().getPackage().getName() + "." + getConventionalName();
        }
	}

	static String stripEnding(String clazz) {

		if (!clazz.endsWith("view")) {
			return clazz;
		}

		return clazz.substring(0, clazz.lastIndexOf("view"));
	}

	/**
	 * @return the relative path to the fxml file derived from the FXML view.
	 *         e.g. The name for the AirhacksView is going to be
	 *         <PATH>/airhacks.fxml.
	 */

	final String getFxmlPath() {
		return fxmlRoot + getConventionalName(".fxml");
	}

	private ResourceBundle getResourceBundle(String name) {
		try {
			return getBundle(name);
		} catch (MissingResourceException ex) {
			return null;
		}
	}

	/**
	 * @return an existing resource bundle, or null
	 */
	public ResourceBundle getResourceBundle() {
		return this.bundle;
	}
}
