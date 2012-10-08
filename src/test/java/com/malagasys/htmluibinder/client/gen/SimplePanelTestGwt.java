package com.malagasys.htmluibinder.client.gen;

import com.google.gwt.junit.client.GWTTestCase;

public class SimplePanelTestGwt extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.malagasys.htmluibinder.Binder";
  }
  
  public void testHappy() {
    SimplePanel p = new SimplePanel();
    assertNotNull(p.connexionButton);
    assertNotNull(p.simpleButton);
    assertNotNull(p.userLogin);
    assertNotNull(p.userPassword);
  }
}
