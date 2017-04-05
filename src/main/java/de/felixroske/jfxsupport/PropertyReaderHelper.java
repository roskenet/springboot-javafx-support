package de.felixroske.jfxsupport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.core.env.Environment;

public class PropertyReaderHelper {

    public static List<String> get(Environment env, String propName) {
        ArrayList<String> list = new ArrayList<>();
        
        String singleProp = env.getProperty(propName);
        if(singleProp != null) {
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
    
    public static <T> void setIfPresent(Environment env, String key, Class<T> type, Consumer<T> function) {
        T value = (T) env.getProperty(key,type);
        if(value != null) {
            function.accept(value);
        }
    }
}
