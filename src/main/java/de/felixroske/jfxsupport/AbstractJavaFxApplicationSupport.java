package de.felixroske.jfxsupport;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

	protected static List<Image> icons = new ArrayList<>();
	
    public static Stage getStage() {
        return stage;
    }
    
    public static Scene getScene() {
        return scene;
    }

    private List<String> propertyArrayReader(Environment env, String propName) {
        ArrayList<String> list = new ArrayList<>();
        int counter=0; 
        String prop = env.getProperty(propName+"[" + counter + "]");
        
        while(prop != null) {
            list.add(prop);
            counter++;
            prop = env.getProperty(propName+"[" + counter + "]");
        }
        
        return list;
    }
    
	@Override
	public void init() throws Exception {
		applicationContext = SpringApplication.run(getClass(), savedArgs);
		List<String> fsImages = propertyArrayReader(applicationContext.getEnvironment(), "javafx.appicons");
		
		if(!fsImages.isEmpty()) {
		    fsImages.forEach((s) -> icons.add(new Image(getClass().getResource(s).toExternalForm())));
		}
		else { // add factory images
		    icons.add(new Image(getClass().getResource("/icons/gear_16x16.png").toExternalForm()));
		    icons.add(new Image(getClass().getResource("/icons/gear_24x24.png").toExternalForm()));
		    icons.add(new Image(getClass().getResource("/icons/gear_36x36.png").toExternalForm()));
		    icons.add(new Image(getClass().getResource("/icons/gear_42x42.png").toExternalForm()));
		    icons.add(new Image(getClass().getResource("/icons/gear_64x64.png").toExternalForm()));
		}
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
		
		stage.getIcons().addAll(icons);
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
