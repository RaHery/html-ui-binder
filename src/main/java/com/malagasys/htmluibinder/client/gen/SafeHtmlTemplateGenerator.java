package com.malagasys.htmluibinder.client.gen;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXParseException;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.resource.ResourceOracle;
import com.google.gwt.dev.util.Util;
import com.google.gwt.dev.util.collect.Sets;
import com.google.gwt.uibinder.rebind.W3cDomHelper;
import com.google.gwt.user.rebind.SourceWriter;
import com.malagasys.htmluibinder.client.HtmlUiTemplate;

class SafeHtmlTemplateGenerator implements PartGenerator {
  private final static Set<String> NO_END_TAG;
  static {
    //from com/google/gxp/compiler/schema/html.xml
    NO_END_TAG = Collections.unmodifiableSet(Sets.create("area", "base",
        "basefont", "br", "col", "frame", "hr", "img", "input", "isindex", 
        "link", "meta", "param", "wbr"));
  }

  private final static String TEMPLATE_SUFFIX = ".html";
  
  @Override
  public void generate(GeneratorContext generatorCtx, JClassType requestedType,
      TreeLogger treeLogger, SourceWriter srcWriter) throws UnableToCompleteException {
    String templateFile = deduceTemplateFile(treeLogger, requestedType);
    String templateContent = readTemplateFileContent(treeLogger, generatorCtx.getResourcesOracle(), templateFile);

    srcWriter.indent();
    srcWriter.println("interface Template extends SafeHtmlTemplates{");
    srcWriter.indentln("@Template(\"" + templateContent + "\")");
    srcWriter.println("SafeHtml html();");
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
  }

  private String readTemplateFileContent(TreeLogger logger, ResourceOracle resourceOracle,
      String templatePath) throws UnableToCompleteException {
    Resource resource = resourceOracle.getResourceMap().get(templatePath);
    if (null == resource) {
      logger.log(Type.ERROR, "Unable to find resource: " + templatePath);
    }

    Document doc = null;
    try {
      String content = Util.readStreamAsString(resource.openContents());
      doc = new W3cDomHelper(logger, resourceOracle).documentFor(content, resource.getPath());
    } catch (IOException iex) {
      logger.log(Type.ERROR, "Error opening resource:" + resource.getLocation(), iex);
      throw new UnableToCompleteException();
    } catch (SAXParseException e) {
      logger.log(Type.ERROR,
          "Error parsing XML (line " + e.getLineNumber() + "): "
              + e.getMessage(), e);
      throw new UnableToCompleteException();
    }

    //Transform back to normalized html text the document.
    StringWriter writer = new StringWriter();
    printNode(doc.getFirstChild(), new PrintWriter(writer));
    return writer.toString();
  }

  private void printNode(Node node, PrintWriter writer) {
    switch(node.getNodeType()) {
      case Node.ELEMENT_NODE:
        printElement((Element) node, writer);
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
    
  private void printElement(Element elem, PrintWriter writer) {
    //Start tag
    String nodeName = elem.getNodeName().toLowerCase();
    writer.printf("<%s", nodeName);
    
    //Print attributes
    NamedNodeMap attributes = elem.getAttributes();
    for(int i = 0; i < attributes.getLength(); ++i) {
      Node attr = attributes.item(i);
      String attrValue = attr.getNodeValue().replace("'", "&#39;");
      writer.printf(" %s='%s'", attr.getNodeName(), attrValue);
    }
    writer.print(">");
    
    //Print children
    NodeList childNodes = elem.getChildNodes();
    if (childNodes != null) {
      for(int i = 0; i < childNodes.getLength(); ++i) {
        printNode(childNodes.item(i), writer);
      }
    }

    if (!NO_END_TAG.contains(nodeName)) {
      writer.printf("</%s>", nodeName);
    }
  }
    
  private String deduceTemplateFile(TreeLogger logger, JClassType interfaceType)
      throws UnableToCompleteException {
    String templateName = null;
    HtmlUiTemplate annotation = interfaceType.getAnnotation(HtmlUiTemplate.class);
    if (annotation == null) {
      // if the interface is defined as a nested class, use the name of the
      // enclosing type
      if (interfaceType.getEnclosingType() != null) {
        interfaceType = interfaceType.getEnclosingType();
      }
      return slashify(interfaceType.getQualifiedBinaryName()) + TEMPLATE_SUFFIX;
    } else {
      templateName = annotation.value();
      if (!templateName.endsWith(TEMPLATE_SUFFIX)) {
        logger.log(Type.ERROR, "Template file name must end with " + TEMPLATE_SUFFIX);
      }

      /*
       * If the template file name (minus suffix) has no dots, make it relative to the binder's
       * package, otherwise slashify the dots
       */
      String unsuffixed = templateName.substring(0, templateName.lastIndexOf(TEMPLATE_SUFFIX));
      if (!unsuffixed.contains(".")) {
        templateName = slashify(interfaceType.getPackage().getName()) + "/" + templateName;
      } else {
        templateName = slashify(unsuffixed) + TEMPLATE_SUFFIX;
      }
    }
    return templateName;
  }

  private static String slashify(String s) {
    return s.replace(".", "/").replace("$", ".");
  }
}
