package jfxtest.annotated;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;

@FXMLView(value="/annotated/fxml/annotated.fxml", 
          bundle="annotated.i18n.annotated",
          css="/annotated/css/style.css")
public class AnnotatedView extends AbstractFxmlView {

}
