package com.malagasys.htmluibinder.client.gen;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;

public final class ElementExt extends Element {
  public final static class Attr extends JavaScriptObject {
    protected Attr() {
    }

    public native String name()/*-{
                               return this.name;
                               }-*/;

    public native String value()/*-{
                                return this.value;
                                }-*/;
  }

  protected ElementExt() {
  }

  public native JsArray<Attr> attributes()/*-{
                                           return this.attributes;
                                           }-*/;
}
