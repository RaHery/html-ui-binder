package com.malagasys.htmluibinder.client.gen;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.malagasys.htmluibinder.client.HtmlUiBinder;
import com.malagasys.htmluibinder.client.HtmlUiField;
import com.malagasys.htmluibinder.client.HtmlUiHandler;
import com.malagasys.htmluibinder.client.HtmlUiTemplate;

/**
 * Test the 
 * @author hermann
 *
 */
public class HtmlUiHandlerTestGwt extends AbstractBaseTest {

  interface MyHandler extends EventHandler {
    void onEvent(MyEvent evt);
  }

  static class MyEvent extends GwtEvent<MyHandler> {
    static final Type<MyHandler> TYPE = new Type<HtmlUiHandlerTestGwt.MyHandler>();
    @Override
    public Type<MyHandler> getAssociatedType() {
      return TYPE;
    }

    @Override
    protected void dispatch(MyHandler handler) {
      handler.onEvent(this);
    }
  }
  
  static class MyWidget extends Widget {
    
    MyWidget() {
      setElement(DOM.createDiv());
    }
    
    HandlerRegistration addMyEventHandler(MyHandler handler) {
      return addHandler(handler, MyEvent.TYPE);
    }
  }
  
  @HtmlUiTemplate("SimplePanel.html")
  interface Binder extends HtmlUiBinder<Panel> {}
  
  static class Panel extends Composite {
    @HtmlUiField
    Button simpleButton;        
    boolean simpleButtonClicked;

    @HtmlUiField("button_2")
    MyWidget widget;
    boolean widgetEventFired;
    
    Panel() {
      Binder b = GWT.create(Binder.class);
      initWidget(b.createAndBindHtml(this));
    }
    
    @HtmlUiHandler("simpleButton")
    void simpleButtonEvent(ClickEvent evt) {
      simpleButtonClicked = true;
    }
    
    @HtmlUiHandler("button_2")
    void myEventFired(MyEvent evt) {
      widgetEventFired = true;
    }
  }
  
  public void testGwtEvent() {
    Panel p = new Panel();
    DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0,
        false, false, false, false), p.simpleButton);

    assertTrue(p.simpleButtonClicked);
  }
  
  public void testCustomEvent() {
    Panel p = new Panel();
    p.widget.fireEvent(new MyEvent());
    
    assertTrue(p.widgetEventFired);
  }
}
