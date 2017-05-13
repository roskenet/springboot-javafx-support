package de.felixroske.jfxsupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The Class AbstractJavaFxApplicationSupport.
 *
 * @author Felix Roske
 */
public abstract class AbstractJavaFxApplicationSupport extends Application {

	private static String[] savedArgs = new String[0];

	private static Class<? extends AbstractFxmlView> savedInitialView;

	private static ConfigurableApplicationContext applicationContext;

	/** The splash screen. */
	private static SplashScreen splashScreen;

	private static List<Image> icons = new ArrayList<>();

	private final BooleanProperty appCtxLoaded = new SimpleBooleanProperty(false);

	public static Stage getStage() {
		return GUIState.getStage();
	}

	public static Scene getScene() {
		return GUIState.getScene();
	}
	
	public static HostServices getAppHostServices() {
            return GUIState.getHostServices();
        }

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#init()
	 */
	@Override
	public void init() throws Exception {
		CompletableFuture.supplyAsync(() -> {
			final ConfigurableApplicationContext ctx = SpringApplication.run(this.getClass(), savedArgs);

			final List<String> fsImages = PropertyReaderHelper.get(ctx.getEnvironment(), "javafx.appicons");

			if (!fsImages.isEmpty()) {
				fsImages.forEach((s) -> icons.add(new Image(getClass().getResource(s).toExternalForm())));
			} else { // add factory images
				icons.add(new Image(getClass().getResource("/icons/gear_16x16.png").toExternalForm()));
				icons.add(new Image(getClass().getResource("/icons/gear_24x24.png").toExternalForm()));
				icons.add(new Image(getClass().getResource("/icons/gear_36x36.png").toExternalForm()));
				icons.add(new Image(getClass().getResource("/icons/gear_42x42.png").toExternalForm()));
				icons.add(new Image(getClass().getResource("/icons/gear_64x64.png").toExternalForm()));
			}
			return ctx;
		}).thenAccept(this::launchApplicationView);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(final Stage stage) throws Exception {
		GUIState.setStage(stage);
		GUIState.setHostServices(this.getHostServices());
		final Stage splashStage = new Stage(StageStyle.UNDECORATED);

		if (AbstractJavaFxApplicationSupport.splashScreen.visible()) {
			final Scene splashScene = new Scene(splashScreen.getParent());
			splashStage.setScene(splashScene);
			splashStage.show();
		}

		final Runnable showMainAndCloseSplash = () -> {
			showInitialView();
			if (AbstractJavaFxApplicationSupport.splashScreen.visible()) {
				splashStage.hide();
				splashStage.setScene(null);
			}
		};

		synchronized (this) {
			if (appCtxLoaded.get() == true) {
				// Spring ContextLoader was faster
				Platform.runLater(showMainAndCloseSplash);
			} else {
				appCtxLoaded.addListener((ov, oVal, nVal) -> {
					Platform.runLater(showMainAndCloseSplash);
				});
			}
		}

	}

	/**
	 * Show initial view.
	 */
	private void showInitialView() {
		final String stageStyle = applicationContext.getEnvironment().getProperty("javafx.stage.style");
		if (stageStyle != null) {
			GUIState.getStage().initStyle(StageStyle.valueOf(stageStyle.toUpperCase()));
		} else {
			GUIState.getStage().initStyle(StageStyle.DECORATED);
		}
		// stage.hide();

		showView(savedInitialView);
	}

	/**
	 * Launch application view.
	 *
	 * @param ctx
	 *            the ctx
	 */
	private void launchApplicationView(final ConfigurableApplicationContext ctx) {
		AbstractJavaFxApplicationSupport.applicationContext = ctx;
		appCtxLoaded.set(true);
	}

	/**
	 * Show view.
	 *
	 * @param newView
	 *            the new view
	 */
	public static void showView(final Class<? extends AbstractFxmlView> newView) {
		final AbstractFxmlView view = applicationContext.getBean(newView);

		if (GUIState.getScene() == null) {
			GUIState.setScene(new Scene(view.getView()));
		} else {
			GUIState.getScene().setRoot(view.getView());
		}
		GUIState.getStage().setScene(GUIState.getScene());

		applyEnvPropsToView();

		GUIState.getStage().getIcons().addAll(icons);
		GUIState.getStage().show();
	}

	/**
	 * Apply env props to view.
	 */
	private static void applyEnvPropsToView() {
		PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), "javafx.title", String.class,
				GUIState.getStage()::setTitle);

		PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), "javafx.stage.width", Double.class,
				GUIState.getStage()::setWidth);

		PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), "javafx.stage.height", Double.class,
				GUIState.getStage()::setHeight);

		PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), "javafx.stage.resizable", Boolean.class,
				GUIState.getStage()::setResizable);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		super.stop();
		if (applicationContext != null) {
			applicationContext.close();
		} // else: someone did it already
	}

	/**
	 * Sets the title. Allows to overwrite values applied during construction at
	 * a later time.
	 *
	 * @param title
	 *            the new title
	 */
	protected static void setTitle(final String title) {
		GUIState.getStage().setTitle(title);
	}

	/**
	 * Launch app.
	 *
	 * @param appClass
	 *            the app class
	 * @param view
	 *            the view
	 * @param args
	 *            the args
	 */
	public static void launchApp(final Class<? extends AbstractJavaFxApplicationSupport> appClass,
			final Class<? extends AbstractFxmlView> view, final String[] args) {
		launchApp(appClass, view, new SplashScreen(), args);
	}

	/**
	 * Launch app.
	 *
	 * @param appClass
	 *            the app class
	 * @param view
	 *            the view
	 * @param splashScreen
	 *            the splash screen
	 * @param args
	 *            the args
	 */
	public static void launchApp(final Class<? extends AbstractJavaFxApplicationSupport> appClass,
			final Class<? extends AbstractFxmlView> view, final SplashScreen splashScreen, final String[] args) {
		savedInitialView = view;
		savedArgs = args;

		if (splashScreen != null) {
			AbstractJavaFxApplicationSupport.splashScreen = splashScreen;
		} else {
			AbstractJavaFxApplicationSupport.splashScreen = new SplashScreen();
		}
		Application.launch(appClass, args);
	}
	
}
