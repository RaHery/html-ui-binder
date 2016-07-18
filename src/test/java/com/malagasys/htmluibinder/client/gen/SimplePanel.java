package com.malagasys.htmluibinder.client.gen;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.malagasys.htmluibinder.client.HtmlUiBinder;
import com.malagasys.htmluibinder.client.HtmlUiField;

public class SimplePanel extends Composite {
  interface Binder extends HtmlUiBinder<SimplePanel> {
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

  public SimplePanel() {
    Binder b = GWT.create(Binder.class);
    initWidget(b.createAndBindHtml(this));
  }
}
