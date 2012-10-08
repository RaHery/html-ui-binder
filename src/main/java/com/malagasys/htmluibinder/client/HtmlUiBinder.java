package com.malagasys.htmluibinder.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * Interface to help generate HtmlBuilder.
 */
public interface HtmlUiBinder<T> {
  Widget createAndBindHtml(T container);
}
