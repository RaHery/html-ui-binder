package com.malagasys.htmluibinder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.malagasys.htmluibinder.client.example.HtmlBinderExample;
import com.malagasys.htmluibinder.client.tmp.ExampleUiBinder;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Binder implements EntryPoint {

  public void onModuleLoad() {
//    RootLayoutPanel.get().add(new ExampleUiBinder());
    RootLayoutPanel.get().add(new HtmlBinderExample());
  }
}
