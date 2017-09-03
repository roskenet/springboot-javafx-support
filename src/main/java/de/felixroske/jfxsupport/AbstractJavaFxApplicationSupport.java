package de.felixroske.jfxsupport;

import java.awt.SystemTray;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The Class AbstractJavaFxApplicationSupport.
 *
 * @author Felix Roske
 */
public abstract class AbstractJavaFxApplicationSupport extends Application {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractJavaFxApplicationSupport.class);

    private static String[] savedArgs = new String[0];

    private static Class<? extends AbstractFxmlView> savedInitialView;

    private static ConfigurableApplicationContext applicationContext;

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
    
    public static SystemTray getSystemTray() {
        return GUIState.getSystemTray();
    }

    /**
     * 
     * @param window
     *            The FxmlView derived class that should be shown.
     * @param mode
     *            See {@code javafx.stage.Modality}.
     */
    public static void showView(final Class<? extends AbstractFxmlView> window, final Modality mode) {
        final AbstractFxmlView view = applicationContext.getBean(window);
        Stage newStage = new Stage();
        
        Scene newScene;
        if(view.getView().getScene() != null) {            
            // This view was already shown so
            // we have a scene for it and use this one.
            newScene = view.getView().getScene();
        }
        else { 
            newScene = new Scene(view.getView());
        }
        
        newStage.setScene(newScene);
        newStage.initModality(mode);
        newStage.initOwner(getStage());
        newStage.setTitle(view.getDefaultTitle());
        newStage.initStyle(view.getDefaultStyle());

        newStage.showAndWait();
    }

  private void loadIcons(ConfigurableApplicationContext ctx)
  {
    try {
      final List<String> fsImages = PropertyReaderHelper.get(ctx.getEnvironment(), "javafx.appicons");

      if (!fsImages.isEmpty()) {
        fsImages.forEach((s) ->
            {
              Image img =  new Image(getClass().getResource(s).toExternalForm());
              icons.add(img);
            }
        );
      } else { // add factory images
        icons.add(new Image(getClass().getResource("/icons/gear_16x16.png").toExternalForm()));
        icons.add(new Image(getClass().getResource("/icons/gear_24x24.png").toExternalForm()));
        icons.add(new Image(getClass().getResource("/icons/gear_36x36.png").toExternalForm()));
        icons.add(new Image(getClass().getResource("/icons/gear_42x42.png").toExternalForm()));
        icons.add(new Image(getClass().getResource("/icons/gear_64x64.png").toExternalForm()));
      }
    } catch (Exception e) {
      LOGGER.error("Failed to load icons: ", e);
    }


  }
  /*
   * (non-Javadoc)
   *
   * @see javafx.application.Application#init()
   */
  @Override
  public void init() throws Exception
  {
    CompletableFuture.supplyAsync(() -> {
     return SpringApplication.run(this.getClass(), savedArgs);
    }).whenComplete((ctx, throwable) -> {
      if (throwable != null) {
        LOGGER.error("Failed to load spring application context: ", throwable);
        Platform.runLater(() -> showErrorAlert(throwable));
      } else {
        Platform.runLater(() -> {
          loadIcons(ctx);
          launchApplicationView(ctx);});
      }
    });
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
			if (appCtxLoaded.get()) {
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
		
		beforeInitialView();
		
		showView(savedInitialView);
	}



    /**
	 * Launch application view.
	 *
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
		try {
			final AbstractFxmlView view = applicationContext.getBean(newView);
			
			if(GUIState.getScene() == null) {
				GUIState.setScene(new Scene(view.getView()));
			}
			else {
				GUIState.getScene().setRoot(view.getView());
			}
			GUIState.getStage().setScene(GUIState.getScene());

			applyEnvPropsToView();

			GUIState.getStage().getIcons().addAll(icons);
			GUIState.getStage().show();

		} catch(Throwable t) {
			LOGGER.error("Failed to load application: ", t);
			showErrorAlert(t);
		}
	}

	/**
	 * Show error alert that close app.
	 *
	 * @param throwable
	 *            cause of error
	 */
	private static void showErrorAlert(Throwable throwable) {
		Alert alert = new Alert(AlertType.ERROR, "Oops! An unrecoverable error occurred.\n" +
				"Please contact your software vendor.\n\n" +
				"The application will stop now.");
		alert.showAndWait().ifPresent(response -> Platform.exit());
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
		
        if(SystemTray.isSupported()) {
            GUIState.setSystemTray(SystemTray.getSystemTray());
         }
        
		Application.launch(appClass, args);
	}

	/**
	 * Gets called after full initialization of Spring application context
	 * and JavaFX platform right before the initial view is shown.
	 * Override this method as a hook to add special code for your app. Especially meant to 
	 * add AWT code to add a system tray icon and behavior by calling 
	 * GUIState.getSystemTray() and modifying it accordingly.
	 * 
	 * By default noop.
	 * 
	 */
	public void beforeInitialView() {
	    
	}
}
