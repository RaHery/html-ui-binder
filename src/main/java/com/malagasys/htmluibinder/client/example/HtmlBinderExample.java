package com.malagasys.htmluibinder.client.example;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.malagasys.htmluibinder.client.HtmlUiBinder;

public class HtmlBinderExample extends Composite {
  
  interface HtmlBinderExampleBinder extends HtmlUiBinder<Widget, HtmlBinderExample> {}

  //  user_login
  TextBox login;

  //  submit_button
  Button submitButton;
  
  public HtmlBinderExample() {
    HtmlBinderExample_UiBinder binder = new HtmlBinderExample_UiBinder();
    initWidget(binder.createAndBindHtml(this));
    DOM.setElementAttribute(login.getElement(), "placeholder", "Email de l'utilisateur");
  }
}
