package de.felixroske.jfxsupport;

import java.util.concurrent.CompletableFuture;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/** Example of displaying a splash page for a standalone JavaFX application */
public class SplashScreen extends Application {
    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;
    private WebView webView;
    private Stage mainStage;
    private static final int SPLASH_WIDTH = 676;
    private static final int SPLASH_HEIGHT = 227;
    private BooleanProperty appCtxLoaded = new SimpleBooleanProperty(false);
    private static ConfigurableApplicationContext appCtx;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void init() {

        CompletableFuture.supplyAsync(() -> {
            ConfigurableApplicationContext ctx = SpringApplication.run(this.getClass(), new String[] {});
            return ctx;
        }).thenAccept(this::afterCtxInit);

        ImageView splash = new ImageView(new Image("http://fxexperience.com/wp-content/uploads/2010/06/logo.png"));
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
        progressText = new Label("Loading hobbits with pie . . .");
        splashLayout = new VBox();
        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        progressText.setAlignment(Pos.CENTER);
        splashLayout.setStyle(
                "-fx-padding: 5; -fx-background-color: cornsilk; -fx-border-width:5; -fx-border-color: linear-gradient(to bottom, chocolate, derive(chocolate, 50%));");
        splashLayout.setEffect(new DropShadow());
    }

    private void afterCtxInit(ConfigurableApplicationContext ctx) {
        SplashScreen.appCtx = ctx;
        appCtxLoaded.set(true);
    }

    private void showInitialView() {
        Platform.runLater(() -> {
        Stage newStage = new Stage();
        
        Pane myPane = new Pane();
        Button myBottin = new Button("Hello Button");
        myPane.getChildren().add(myBottin);
        
        Scene myScene = new Scene(myPane);
        initialStage.setScene(myScene);
        });
    }
    
    private static Stage initialStage; 
    
    @Override
    public void start(final Stage initStage) throws Exception {
        SplashScreen.initialStage = initStage;
        showSplash(initStage);
        // showMainStage();
        synchronized(this) {
        if (appCtxLoaded.get() == true) {
            System.out.println("No LISTENER: AppCtx Loaded");
            showInitialView();
        } else {
            appCtxLoaded.addListener((ov, oVal, nVal) -> {
                System.out.println("AppCtx Loaded");
                showInitialView();
            });
        }
        }
        // webView.getEngine().documentProperty().addListener(new
        // ChangeListener<Document>() {
        // @Override public void changed(ObservableValue<? extends Document>
        // observableValue, Document document, Document document1) {
        // if (initStage.isShowing()) {
        // loadProgress.progressProperty().unbind();
        // loadProgress.setProgress(1);
        // progressText.setText("All hobbits are full.");
        // mainStage.setIconified(false);
        // initStage.toFront();
        // FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2),
        // splashLayout);
        // fadeSplash.setFromValue(1.0);
        // fadeSplash.setToValue(0.0);
        // fadeSplash.setOnFinished(new EventHandler<ActionEvent>() {
        // @Override public void handle(ActionEvent actionEvent) {
        // initStage.hide();
        // }
        // });
        // fadeSplash.play();
        // }
        // }
        // });
    }

    private void showMainStage() {
        mainStage = new Stage(StageStyle.DECORATED);
        mainStage.setTitle("FX Experience");
        mainStage.setIconified(true);

        // create a WebView.
        // webView = new WebView();
        // webView.getEngine().load("http://fxexperience.com/");
        // loadProgress.progressProperty().bind(webView.getEngine().getLoadWorker().workDoneProperty().divide(100));

        // layout the scene.
        Scene scene = new Scene(webView, 1000, 600);
        webView.prefWidthProperty().bind(scene.widthProperty());
        webView.prefHeightProperty().bind(scene.heightProperty());
        mainStage.setScene(scene);
        mainStage.show();
    }

    private void showSplash(Stage initStage) {
        Scene splashScene = new Scene(splashLayout);
        initStage.initStyle(StageStyle.UNDECORATED);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        initStage.setScene(splashScene);
        initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        initStage.show();
    }
}
