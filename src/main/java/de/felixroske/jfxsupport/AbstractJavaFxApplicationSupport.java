package de.felixroske.jfxsupport;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

/**
 * The Class AbstractJavaFxApplicationSupport.
 *
 * @author Felix Roske
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractJavaFxApplicationSupport extends Application {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractJavaFxApplicationSupport.class);

    private static String[] savedArgs = new String[0];

    static Class<? extends AbstractFxmlView> savedInitialView;

    static SplashScreen splashScreen;

    private static ConfigurableApplicationContext applicationContext;

    private static List<Image> icons = new ArrayList<>();

    private static Consumer<Throwable> errorAction = defaultErrorAction();

    private final List<Image> defaultIcons = new ArrayList<>();

    private final CompletableFuture<Runnable> splashIsShowing;

    protected AbstractJavaFxApplicationSupport() {
        splashIsShowing = new CompletableFuture<>();
    }

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

    private void loadIcons(ConfigurableApplicationContext ctx) {
        try {
            final List<String> fsImages = PropertyReaderHelper.get(ctx.getEnvironment(), Constant.KEY_APPICONS);
            if (!fsImages.isEmpty()) {
                fsImages.forEach((s) -> {
                    Image img = new Image(getClass().getResource(s).toExternalForm());
                    icons.add(img);
                });
            } else { // add factory images
                icons.addAll(defaultIcons);
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
    public void init() throws Exception {
        // Load in JavaFx Thread and reused by Completable Future, but should no be a big deal.
        defaultIcons.addAll(loadDefaultIcons());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask<ConfigurableApplicationContext> configurableApplicationContextFutureTask = new FutureTask<>(() -> {
            SpringApplication customSpringApplication = generatedCustomSpringApplication();
            if (customSpringApplication == null) {
                return SpringApplication.run(this.getClass(), savedArgs);
            } else {
                return customSpringApplication.run(savedArgs);
            }
        });
        executorService.execute(configurableApplicationContextFutureTask);
        CompletableFuture.supplyAsync(() ->
                {
                    try {
                        return configurableApplicationContextFutureTask.get();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
        ).whenComplete((ctx, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Failed to load spring application context: ", throwable);
                Platform.runLater(() -> errorAction.accept(throwable));
            } else {
                Platform.runLater(() -> {
                    loadIcons(ctx);
                    launchApplicationView(ctx);
                });
            }
            executorService.shutdown();
        }).thenAcceptBothAsync(splashIsShowing, (ctx, closeSplash) -> {
            Platform.runLater(closeSplash);
        });
    }

    protected SpringApplication generatedCustomSpringApplication() {
        return null;
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
        final Stage splashStage = new Stage(StageStyle.TRANSPARENT);

        if (AbstractJavaFxApplicationSupport.splashScreen.visible()) {
            final Scene splashScene = new Scene(splashScreen.getParent(), Color.TRANSPARENT);
            splashStage.setScene(splashScene);
            splashStage.getIcons().addAll(defaultIcons);
            splashStage.initStyle(StageStyle.TRANSPARENT);
            beforeShowingSplash(splashStage);
            splashStage.show();
        }

        splashIsShowing.complete(() -> {
            showInitialView();
            if (AbstractJavaFxApplicationSupport.splashScreen.visible()) {
                splashStage.hide();
                splashStage.setScene(null);
            }
        });
    }

    /**
     * Show initial view.
     */
    private void showInitialView() {
        final String stageStyle = applicationContext.getEnvironment().getProperty(Constant.KEY_STAGE_STYLE);
        if (stageStyle != null) {
            GUIState.getStage().initStyle(StageStyle.valueOf(stageStyle.toUpperCase()));
        } else {
            GUIState.getStage().initStyle(StageStyle.DECORATED);
        }

        beforeInitialView(GUIState.getStage(), applicationContext);

        showInitialView(savedInitialView);
    }


    /**
     * Launch application view.
     */
    private void launchApplicationView(final ConfigurableApplicationContext ctx) {
        AbstractJavaFxApplicationSupport.applicationContext = ctx;
    }

    /**
     * Show view.
     *
     * @param newView the new view
     */
    public static void showInitialView(final Class<? extends AbstractFxmlView> newView) {
        try {
            final AbstractFxmlView view = applicationContext.getBean(newView);
            view.initFirstView();
            applyEnvPropsToView();

            GUIState.getStage().getIcons().addAll(icons);
            GUIState.getStage().show();

        } catch (Throwable t) {
            LOGGER.error("Failed to load application: ", t);
            errorAction.accept(t);
        }
    }

    protected static void setErrorAction(Consumer<Throwable> callback) {
        errorAction = callback;
    }

    /**
     * Default error action that shows a message and closes the app.
     */
    private static Consumer<Throwable> defaultErrorAction() {
        return e -> {
            Alert alert = new Alert(AlertType.ERROR, "Oops! An unrecoverable error occurred.\n" +
                    "Please contact your software vendor.\n\n" +
                    "The application will stop now.");
            alert.showAndWait().ifPresent(response -> Platform.exit());
        };
    }

    /**
     * Apply env props to view.
     */
    private static void applyEnvPropsToView() {
        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_TITLE, String.class,
                GUIState.getStage()::setTitle);

        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_STAGE_WIDTH, Double.class,
                GUIState.getStage()::setWidth);

        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_STAGE_HEIGHT, Double.class,
                GUIState.getStage()::setHeight);

        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_STAGE_RESIZABLE, Boolean.class,
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
     * @param title the new title
     */
    protected static void setTitle(final String title) {
        GUIState.getStage().setTitle(title);
    }

    /**
     * Launch app.
     *
     * @param appClass the app class
     * @param view     the view
     * @param args     the args
     */
    public static void launch(final Class<? extends Application> appClass,
                              final Class<? extends AbstractFxmlView> view, final String[] args) {

        launch(appClass, view, new SplashScreen(), args);
    }

    /**
     * Launch app.
     *
     * @param appClass the app class
     * @param view     the view
     * @param args     the args
     * @deprecated To be more in line with javafx.application please use launch
     */
    @Deprecated
    public static void launchApp(final Class<? extends Application> appClass,
                                 final Class<? extends AbstractFxmlView> view, final String[] args) {

        launch(appClass, view, new SplashScreen(), args);
    }

    /**
     * Launch app.
     *
     * @param appClass     the app class
     * @param view         the view
     * @param splashScreen the splash screen
     * @param args         the args
     */
    public static void launch(final Class<? extends Application> appClass,
                              final Class<? extends AbstractFxmlView> view, final SplashScreen splashScreen, final String[] args) {
        savedInitialView = view;
        savedArgs = args;

        if (splashScreen != null) {
            AbstractJavaFxApplicationSupport.splashScreen = splashScreen;
        } else {
            AbstractJavaFxApplicationSupport.splashScreen = new SplashScreen();
        }

        if (SystemTray.isSupported()) {
            GUIState.setSystemTray(SystemTray.getSystemTray());
        }

        Application.launch(appClass, args);
    }

    /**
     * Launch app.
     *
     * @param appClass     the app class
     * @param view         the view
     * @param splashScreen the splash screen
     * @param args         the args
     * @deprecated To be more in line with javafx.application please use launch
     */
    @Deprecated
    public static void launchApp(final Class<? extends Application> appClass,
                                 final Class<? extends AbstractFxmlView> view, final SplashScreen splashScreen, final String[] args) {
        launch(appClass, view, splashScreen, args);
    }

    /**
     * Gets called after full initialization of Spring application context
     * and JavaFX platform right before the initial view is shown.
     * Override this method as a hook to add special code for your app. Especially meant to
     * add AWT code to add a system tray icon and behavior by calling
     * GUIState.getSystemTray() and modifying it accordingly.
     * <p>
     * By default noop.
     *
     * @param stage can be used to customize the stage before being displayed
     * @param ctx   represents spring ctx where you can loog for beans.
     */
    public void beforeInitialView(final Stage stage, final ConfigurableApplicationContext ctx) {
    }

    public void beforeShowingSplash(Stage splashStage) {

    }

    public Collection<Image> loadDefaultIcons() {
        return Arrays.asList(new Image(getClass().getResource("/icons/gear_16x16.png").toExternalForm()),
                new Image(getClass().getResource("/icons/gear_24x24.png").toExternalForm()),
                new Image(getClass().getResource("/icons/gear_36x36.png").toExternalForm()),
                new Image(getClass().getResource("/icons/gear_42x42.png").toExternalForm()),
                new Image(getClass().getResource("/icons/gear_64x64.png").toExternalForm()));
    }
}
