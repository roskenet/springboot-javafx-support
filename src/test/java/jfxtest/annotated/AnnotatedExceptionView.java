package jfxtest.annotated;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;

@FXMLView(value="/i_do_not_exist.fxml", 
          bundle="annotated.i18n.annotated",
          css="/annotated/css/style.css")
public class AnnotatedExceptionView extends AbstractFxmlView {

}
