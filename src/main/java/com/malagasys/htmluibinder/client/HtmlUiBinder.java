package com.malagasys.htmluibinder.client;




/**
 * Marker interface to identify HtmlBuilder.
 * 
 * This marker interface will trigger the custom GWT generator.
 * @author hermann
 */
public interface HtmlUiBinder<O, T> {
  O createAndBindHtml(T container);
}
