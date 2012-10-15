package com.malagasys.htmluibinder.client.gen;


public class SimplePanelTestGwt extends AbstractBaseTest {

  public void testHappy() {
    SimplePanel p = new SimplePanel();
    assertNotNull(p.connexionButton);
    assertNotNull(p.simpleButton);
    assertNotNull(p.userLogin);
    assertNotNull(p.userPassword);
  }
}
