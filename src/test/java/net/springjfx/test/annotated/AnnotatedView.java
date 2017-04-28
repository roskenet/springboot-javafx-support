package net.springjfx.test.annotated;

import net.springjfx.AbstractFxmlView;
import net.springjfx.FXMLView;

@FXMLView(value="/annotated/fxml/annotated.fxml", 
          bundle="annotated.i18n.annotated",
          css="/annotated/css/style.css")
public class AnnotatedView extends AbstractFxmlView {

}
