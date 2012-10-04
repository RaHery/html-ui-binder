package com.malagasys.htmluibinder.client.example;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class HtmlBinderExample_UiBinder implements HtmlBinderExample.HtmlBinderExampleBinder {
  
  interface Template extends SafeHtmlTemplates {
    @Template("<div><p> <label id=\"userlogin_label\"> <em>Email ou nom d’utilisateur</em> <input type=\"text\" id=\"user_login\" value=\"\"/></label></p><p><button type=\"submit\" name=\"submit\" id=\"submit_button\"><span>Connexion</span></button></p></div>")
    SafeHtml html();
  }

  @Override
  public Widget createAndBindHtml(HtmlBinderExample container) {
    Template template = GWT.create(Template.class);
    SafeHtml safeHtml = template.html();
    HTMLPanel htmlPanel = new HTMLPanel(safeHtml);
    
    //Initialize the value of the field and assign them
    //Provided ==> ??
    
    //Keep classes of elements.
    container.login = new TextBox();
    htmlPanel.addAndReplaceElement(container.login, "user_login");
    
    container.submitButton = new Button("RunButton");
    htmlPanel.addAndReplaceElement(container.submitButton, "submit_button");
    
    return htmlPanel;
  }
}
