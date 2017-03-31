package de.felixroske.jfxsupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Felix Roske
 */
public abstract class AbstractJavaFxApplicationSupport extends Application {
    
    private static String[] savedArgs = new String[0];

    private static Class<? extends AbstractFxmlView> savedInitialView;

    private static ConfigurableApplicationContext applicationContext;

    private static Stage stage;
    private static Scene scene;
    private static SplashScreen splashScreen;

    protected static List<Image> icons = new ArrayList<>();
    private BooleanProperty appCtxLoaded = new SimpleBooleanProperty(false);

    public static Stage getStage() {
        return stage;
    }

    public static Scene getScene() {
        return scene;
    }

    @Override
    public void init() throws Exception {
        CompletableFuture.supplyAsync(() -> {
            ConfigurableApplicationContext ctx = SpringApplication.run(this.getClass(), savedArgs);
            
            List<String> fsImages = PropertyArrayReader.get(ctx.getEnvironment(), "javafx.appicons");
            
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

    @Override
    public void start(Stage stage) throws Exception {
        AbstractJavaFxApplicationSupport.stage = stage;
        Stage splashStage = new Stage(StageStyle.UNDECORATED); 
        
        if(AbstractJavaFxApplicationSupport.splashScreen.visible()) {
            Scene splashScene = new Scene(splashScreen.getParent());
            splashStage.setScene(splashScene);
//            splashStage.centerOnScreen();
            splashStage.show();
        }
        synchronized(this) {
        if (appCtxLoaded.get() == true) {
            // Spring ContextLoader was faster
            Platform.runLater(() -> {
                showInitialView();
                if(AbstractJavaFxApplicationSupport.splashScreen.visible()) {
                    splashStage.hide();
                }
            });
        } else {
            appCtxLoaded.addListener((ov, oVal, nVal) -> {
                Platform.runLater(() -> {
                    showInitialView();
                    if(AbstractJavaFxApplicationSupport.splashScreen.visible()) {
                        splashStage.hide();
                    }
                });
            });
        }
        }
        
    }

    private void showInitialView() {
        String stageStyle = applicationContext.getEnvironment().getProperty("javafx.stage.style");
        if(stageStyle != null) {
            stage.initStyle(StageStyle.valueOf(stageStyle.toUpperCase())); 
        }
        else {
            stage.initStyle(StageStyle.DECORATED);
        }
//        stage.hide();
        
        showView(savedInitialView);
    }
    
    private void launchApplicationView(ConfigurableApplicationContext ctx) {
        AbstractJavaFxApplicationSupport.applicationContext = ctx;
        appCtxLoaded.set(true);
    }
    
    public static void showView(Class<? extends AbstractFxmlView> newView) {
        AbstractFxmlView view = applicationContext.getBean(newView);
        String title = applicationContext.getEnvironment().getProperty("javafx.title");

        if (scene == null) {
            scene = new Scene(view.getView());
        } else {
            scene.setRoot(view.getView());
        }

        stage.setScene(scene);
        if (title != null) {
            setTitle(title);
        }

        Long width = applicationContext.getEnvironment().getProperty("javafx.stage.width", Long.class);
        if (width != null) {
            stage.setWidth(width);
        }
        Long height = applicationContext.getEnvironment().getProperty("javafx.stage.height", Long.class);
        if (height != null) {
            stage.setHeight(height);
        }
        Boolean resizable = applicationContext.getEnvironment().getProperty("javafx.stage.resizable", Boolean.class);
        if (resizable != null) {
            stage.setResizable(resizable);
        }
        stage.getIcons().addAll(icons);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (applicationContext != null) {
            applicationContext.close();
        } // else: someone did it already
    }

    protected static void setTitle(String title) {
        stage.setTitle(title);
    }

    protected static void launchApp(Class<? extends AbstractJavaFxApplicationSupport> appClass,
            Class<? extends AbstractFxmlView> view, String[] args) {
       launchApp(appClass, view, new SplashScreen(), args); 
    }
    
    protected static void launchApp(Class<? extends AbstractJavaFxApplicationSupport> appClass,
            Class<? extends AbstractFxmlView> view, SplashScreen splashScreen, String[] args) {
        savedInitialView = view;
        savedArgs = args;

        if(splashScreen != null) { 
            AbstractJavaFxApplicationSupport.splashScreen = splashScreen;
        }
        else {
            AbstractJavaFxApplicationSupport.splashScreen = new SplashScreen();
        }
        Application.launch(appClass, args);
    }

}
