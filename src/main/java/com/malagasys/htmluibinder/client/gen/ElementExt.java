package com.malagasys.htmluibinder.client.gen;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;

final class ElementExt extends Element {
  final static class Attr extends JavaScriptObject {
    protected Attr() {
    }

    native String name()/*-{
                        return this.name;
                        }-*/;

    native String value()/*-{
                         return this.value;
                         }-*/;
  }

  protected ElementExt() {
  }

  native JsArray<Attr> attributes()/*-{
                                   return this.attributes;
                                   }-*/;
}
