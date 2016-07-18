package com.malagasys.htmluibinder.client.gen;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.util.collect.Sets;
import com.google.gwt.user.rebind.SourceWriter;

class SafeHtmlTemplateBuilder implements PartBuilder {
  private final static Set<String> NO_END_TAG;
  static {
    // from com/google/gxp/compiler/schema/html.xml
    NO_END_TAG =
        Collections.unmodifiableSet(Sets.create("area", "base", "basefont", "br", "col", "frame",
            "hr", "img", "input", "isindex", "link", "meta", "param", "wbr"));
  }

  private final static String HTML_UI_ID_ATTRIBUTE_NAME = "data-fieldid";

  private int id_counter = 100;

  @Override
  public void generate(HtmlUiGeneratorContext context) throws UnableToCompleteException {
    StringWriter docWriter = new StringWriter();
    printNode(context.getTreeLogger(), context.getDocument().getFirstChild(), new PrintWriter(
        docWriter), context);
    String templateContent = docWriter.toString();

    SourceWriter srcWriter = context.getSourceWriter();
    srcWriter.println("interface Template extends SafeHtmlTemplates{");
    srcWriter.indent();
    srcWriter.println("@Template(\"" + templateContent + "\")");
    srcWriter.println("SafeHtml html();");
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
  }

  private void printNode(TreeLogger logger, Node node, PrintWriter writer,
      HtmlUiGeneratorContext context) throws UnableToCompleteException {
    switch (node.getNodeType()) {
      case Node.ELEMENT_NODE:
        printElement(logger, (Element) node, writer, context);
        break;

      case Node.TEXT_NODE:
        writer.print(((Text) node).getNodeValue().trim());
        break;

      case Node.COMMENT_NODE:
        break;

      case Node.CDATA_SECTION_NODE:
        writer.print(((CDATASection) node).getNodeValue().trim());
        break;

      case Node.ENTITY_NODE:
      case Node.ENTITY_REFERENCE_NODE:
      case Node.ATTRIBUTE_NODE:
      case Node.DOCUMENT_NODE:
      case Node.DOCUMENT_FRAGMENT_NODE:
      case Node.NOTATION_NODE:
      case Node.PROCESSING_INSTRUCTION_NODE:
      default: {
        // None of these are expected node types.
        throw new RuntimeException("Unexpected XML node");
      }
    }
  }

  private void printElement(TreeLogger logger, Element elem, PrintWriter writer,
      HtmlUiGeneratorContext context) throws UnableToCompleteException {
    /*
     * While printing the content of the element into the writer, create a new `id' for element
     * having an `htmlui:id' but without id.
     */
    // Start tag
    String nodeName = elem.getNodeName().toLowerCase();
    writer.printf("<%s", nodeName);

    // Print attributes
    boolean containsHtmlUiId = false;
    boolean containsId = false;
    String htmluiId = null;
    String id = null;
    NamedNodeMap attributes = elem.getAttributes();
    for (int i = 0; i < attributes.getLength(); ++i) {
      Node attr = attributes.item(i);
      String attrValue = attr.getNodeValue().replace("'", "&#39;");
      writer.printf(" %s='%s'", attr.getNodeName(), attrValue);

      // Capture htmlui:id attribute
      if (attr.getNodeName().equals(HTML_UI_ID_ATTRIBUTE_NAME)) {
        if (containsHtmlUiId) {
          logger.log(Type.ERROR, "Duplicate `htmlui:id' found in the tag `" + nodeName + "'");
          throw new UnableToCompleteException();
        }
        containsHtmlUiId = true;
        htmluiId = attrValue;
      }

      // Capture id attribute
      if (attr.getNodeName().equals("id")) {
        containsId = true;
        id = attrValue;
      }
    }

    // If there htmlui:id attribute, add a mapping of htmlui:id ==> id
    if (containsHtmlUiId) {
      if (!containsId) {
        id = "__html_id__" + id_counter++;
        writer.printf(" id='%s'", id);
      }
      context.putId(htmluiId, id);
    }

    // Close the tag.
    writer.print(">");

    // Print children
    NodeList childNodes = elem.getChildNodes();
    if (childNodes != null) {
      for (int i = 0; i < childNodes.getLength(); ++i) {
        printNode(logger, childNodes.item(i), writer, context);
      }
    }

    if (!NO_END_TAG.contains(nodeName)) {
      writer.printf("</%s>", nodeName);
    }
  }
}
