package de.felixroske.jfx.support;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Felix Roske 
 */
public abstract class AbstractJavaFxApplicationSupport extends Application {

	private static String[] savedArgs;

	private static Class<? extends AbstractFxmlView> savedInitialView;

	private static ConfigurableApplicationContext applicationContext;

	private static Stage stage;
	private static Scene scene; // = new Scene();

	@Override
	public void init() throws Exception {
		applicationContext = SpringApplication.run(getClass(), savedArgs);
		applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
	}

	@Override
	public void start(Stage stage) throws Exception {
		AbstractJavaFxApplicationSupport.stage = stage;

		showView(savedInitialView);
	}

	public void showView(Class<? extends AbstractFxmlView> newView) {
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
		applicationContext.close();
	}

	protected static void launchApp(Class<? extends AbstractJavaFxApplicationSupport> appClass,
			Class<? extends AbstractFxmlView> view, String[] args) {
		savedInitialView = view;
		savedArgs = args;
		Application.launch(appClass, args);
	}
}
