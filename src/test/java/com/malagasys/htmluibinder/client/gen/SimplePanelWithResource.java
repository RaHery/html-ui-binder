package com.malagasys.htmluibinder.client.gen;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.malagasys.htmluibinder.client.HtmlUiBinder;
import com.malagasys.htmluibinder.client.HtmlUiField;
import com.malagasys.htmluibinder.client.HtmlUiTemplate;

public class SimplePanelWithResource extends Composite {

  @HtmlUiTemplate(value = "SimplePanelWithResource.html", resourceClass = "com.malagasys.htmluibinder.client.gen.SimplePanelResource")
  interface Binder extends HtmlUiBinder<SimplePanelWithResource> {
  }

  // Explicit name of the field.
  @HtmlUiField("userlogin")
  TextBox userLogin;

  @HtmlUiField("userpassword")
  TextBox userPassword;

  // Implicit ui field.
  @HtmlUiField
  Button simpleButton;

  @HtmlUiField("button_2")
  Button connexionButton;

  public SimplePanelWithResource() {
    Binder b = GWT.create(Binder.class);
    initWidget(b.createAndBindHtml(this));
  }
}
