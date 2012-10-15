package com.malagasys.htmluibinder.client.gen;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.malagasys.htmluibinder.client.HtmlUiBinder;
import com.malagasys.htmluibinder.client.HtmlUiField;
import com.malagasys.htmluibinder.client.HtmlUiTemplate;

public class PanelWidgetWithoutId extends AbstractBaseTest {
  
  @HtmlUiTemplate("Panel_WidgetWithoutId.html")
  interface Binder extends HtmlUiBinder<Panel> {}
  
  class Panel extends Composite {
    @HtmlUiField("userlogin")
    TextBox userName;
    
    @HtmlUiField("userpassword")
    TextBox password;
    
    Panel() {
      Binder uiBinder = GWT.create(Binder.class);
      initWidget(uiBinder.createAndBindHtml(this));
    }
  }
  
  public void testInit() {
    Panel p = new Panel();
    assertNotNull(p.password);
    assertNotNull(p.userName);
  }
}
