package com.malagasys.htmluibinder.client.tmp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ExampleUiBinder extends Composite {

  private static ExampleUiBinderUiBinder uiBinder = GWT.create(ExampleUiBinderUiBinder.class);

  interface ExampleUiBinderUiBinder extends UiBinder<Widget, ExampleUiBinder> {
  }
  
  @UiField
  InputElement loginInputElement;
  private TextBox loginInput;
  
  @UiField
  TextBox actualTextBox;

  public ExampleUiBinder() {
    initWidget(uiBinder.createAndBindUi(this));
//    loginInput = TextBox.wrap(loginInputElement);
    actualTextBox.setText("This is a text.");
  }

  public ExampleUiBinder(String firstName) {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
