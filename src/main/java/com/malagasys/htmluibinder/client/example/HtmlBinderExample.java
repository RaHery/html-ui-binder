package com.malagasys.htmluibinder.client.example;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.malagasys.htmluibinder.client.HtmlUiBinder;
import com.malagasys.htmluibinder.client.HtmlUiField;

public class HtmlBinderExample extends Composite {
  
  interface HtmlBinderExampleBinder extends HtmlUiBinder<Widget, HtmlBinderExample> {}

  @HtmlUiField
  TextBox login;

  @HtmlUiField
  TextBox userPassword;

  @HtmlUiField(id="submit_button")
  Button submitButton;
  
  public HtmlBinderExample() {
    HtmlBinderExampleBinder binder = GWT.create(HtmlBinderExampleBinder.class);
    initWidget(binder.createAndBindHtml(this));
    DOM.setElementAttribute(login.getElement(), "placeholder", "Email de l'utilisateur");
    submitButton.setText("Clicker-o eto!");
  }
}
