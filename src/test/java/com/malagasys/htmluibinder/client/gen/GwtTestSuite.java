package com.malagasys.htmluibinder.client.gen;

import junit.framework.Test;

import com.google.gwt.junit.tools.GWTTestSuite;

public class GwtTestSuite extends GWTTestSuite {
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("`Html ui binder'");
    suite.addTestSuite(SimplePanelTestGwt.class);
    return suite;
  }
}
