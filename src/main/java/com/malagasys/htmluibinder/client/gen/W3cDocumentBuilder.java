package com.malagasys.htmluibinder.client.gen;

import java.util.Stack;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.resource.ResourceOracle;

/**
 * Uses SAX events to construct a DOM Document.
 */
// derived from com/google/gwt/uibinder/rebind/W3cDocumentBuilder
class W3cDocumentBuilder extends DefaultHandler2 {
  private final Document document;
  private final Stack<Node> eltStack = new Stack<Node>();
  private final TreeLogger logger;

  public W3cDocumentBuilder(TreeLogger logger, String pathBase, ResourceOracle resourceOracle)
      throws ParserConfigurationException {
    this.logger = logger;
    document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    eltStack.push(document);
  }

  /**
   * Appends to the existing Text node, if possible.
   */
  @Override
  public void characters(char[] ch, int start, int length) {
    Node current = eltStack.peek();
    if (current.getChildNodes().getLength() == 1
        && current.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
      Text text = (Text) current.getChildNodes().item(0);
      text.appendData(new String(ch, start, length));
    } else {
      Text text = document.createTextNode(new String(ch, start, length));
      eltStack.peek().appendChild(text);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
    Node elt = eltStack.pop();
    assert elt.getLocalName().equals(localName);
  }

  @Override
  public void error(SAXParseException exception) {
    logger.log(TreeLogger.ERROR, exception.getMessage());
    logger.log(TreeLogger.DEBUG, "SAXParseException", exception);
  }

  @Override
  public void fatalError(SAXParseException exception) {
    /*
     * Fatal errors seem to be no scarier than error errors, and simply happen due to badly formed
     * XML.
     */
    logger.log(TreeLogger.ERROR, exception.getMessage());
    logger.log(TreeLogger.DEBUG, "SAXParseException", exception);
  }

  public Document getDocument() {
    return document;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    Element elt = document.createElement(qName);
    eltStack.peek().appendChild(elt);
    eltStack.push(elt);

    for (int i = 0, j = attributes.getLength(); i < j; i++) {
      elt.setAttribute(attributes.getQName(i), attributes.getValue(i));
    }
  }

  @Override
  public void warning(SAXParseException exception) {
    logger.log(TreeLogger.WARN, exception.getMessage());
    logger.log(TreeLogger.DEBUG, "SAXParseException", exception);
  }
}