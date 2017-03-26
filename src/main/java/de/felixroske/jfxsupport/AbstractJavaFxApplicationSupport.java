package de.felixroske.jfxsupport;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Felix Roske 
 */
public abstract class AbstractJavaFxApplicationSupport extends Application {

	private static String[] savedArgs = new String[0];

	private static Class<? extends AbstractFxmlView> savedInitialView;

	private static ConfigurableApplicationContext applicationContext;

	private static Stage stage;
	private static Scene scene; 
   
    public static Stage getStage() {
        return stage;
    }
    
    public static Scene getScene() {
        return scene;
    }
    
	@Override
	public void init() throws Exception {
		applicationContext = SpringApplication.run(getClass(), savedArgs);
	}

	@Override
	public void start(Stage stage) throws Exception {
		AbstractJavaFxApplicationSupport.stage = stage;
		showView(savedInitialView);
	}

	public static void showView(Class<? extends AbstractFxmlView> newView) {
		AbstractFxmlView view = applicationContext.getBean(newView);
		stage.titleProperty().bind(view.titleProperty());
		if (scene == null) {
			scene = new Scene(view.getView());
		}
		else {  
			scene.setRoot(view.getView());
		}
		
		// stage.setTitle(windowTitle);
		stage.setScene(scene);
//		stage.setResizable(true);
//		stage.centerOnScreen();
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		if(applicationContext != null) {
		    applicationContext.close();
		} //else: someone did it already
	}

	protected static void launchApp(Class<? extends AbstractJavaFxApplicationSupport> appClass,
			Class<? extends AbstractFxmlView> view, String[] args) {
		savedInitialView = view;
		savedArgs = args;
		Application.launch(appClass, args);
	}

}
