package de.felixroske.jfxsupport;

import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class AbstractFxmlController {

  public AbstractFxmlController() {

  }

  void setFields(FXMLLoader loader) {
    loader.getNamespace().addListener((MapChangeListener<? super String, ? super Object>) value -> {
      Field field = getFxmlField(value.getKey());
      if (field == null) {
        return;
      }
      if (value.wasAdded()) {
        try {
          field.set(this, loader.getNamespace().get(value.getKey()));
        } catch (IllegalAccessException e) {
          throw new IllegalStateException("Failed to set field for controller");
        }
      } else if (value.wasRemoved()) {
        try {
          field.set(this, null);
        } catch (IllegalAccessException e) {
          throw new IllegalStateException("Failed to set field for controller");
        }
      }
    });
  }

  private Field getFxmlField(String fieldName) {
    Field field;
    try {
      field = getClass().getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      return null;
    }

    if (Modifier.isPublic(field.getModifiers())) {
      return field;
    }
    if (field.getAnnotation(FXML.class) != null) {
      field.setAccessible(true);
      return field;
    }

    return null;

  }

}
