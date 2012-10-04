package com.malagasys.htmluibinder.client.tmp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.UIObject;

public class ExampleUiBinderHtml extends UIObject {

  private static ExampleUiBinderHtmlUiBinder uiBinder = GWT
      .create(ExampleUiBinderHtmlUiBinder.class);

  interface ExampleUiBinderHtmlUiBinder extends UiBinder<Element, ExampleUiBinderHtml> {
  }

  @UiField
  SpanElement nameSpan;

  public ExampleUiBinderHtml(String firstName) {
    setElement(uiBinder.createAndBindUi(this));
    nameSpan.setInnerText(firstName);
  }

}
