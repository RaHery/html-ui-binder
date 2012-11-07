package com.malagasys.htmluibinder.client.gen;

import java.io.IOException;
import java.io.PrintWriter;

import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.resource.ResourceOracle;
import com.google.gwt.dev.util.Util;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.malagasys.htmluibinder.client.HtmlUiBinder;
import com.malagasys.htmluibinder.client.HtmlUiTemplate;

public class HtmlUiBinderGenerator extends Generator {
  private final static String BINDER_SUFFIX = "_HtmlUiBinder";
  private final static String TEMPLATE_SUFFIX = ".html";

  @Override
  public String generate(TreeLogger logger, GeneratorContext context, String typeName)
      throws UnableToCompleteException {
    // Grab the information about the class requested
    TypeOracle typeOracle = context.getTypeOracle();
    JClassType requestedType = typeOracle.findType(typeName);

    // Check if already generated.
    if (typeOracle.findType(requestedType.getPackage().getName() + "."
        + getBinderSimpleName(requestedType)) != null) {
      return null;
    }

    // Get a writer to the output class.
    SourceWriter sourceWriter = getSourceWriter(logger, context, requestedType);

    // Source writer is null if the file has already been generated.
    if (sourceWriter != null) {
      logger.log(Type.INFO, "Generating HtmlBinder for type `" + typeName + "'" + " Context="
          + context);
      Document htmlDocument = readDocument(logger, context.getResourcesOracle(), requestedType);
      
      PartBuilder[] generators = new PartBuilder[] {
          new SafeHtmlTemplateBuilder(),
          new WidgetFieldsBuilder(),
          new EventHandlerBuilder(),
          new CreateAndBindUiBuilder(),
      };
      
      //Do generation
      HtmlUiGeneratorContext ctx = new HtmlUiGeneratorContext(htmlDocument, context, 
          requestedType, logger, sourceWriter);
      for (PartBuilder g : generators) {
        g.generate(ctx);
      }

      //Done. Commit!
      sourceWriter.commit(logger);
    }

    return requestedType.getPackage().getName() + "." + getBinderSimpleName(requestedType);
  }

  private String getBinderSimpleName(JClassType requestedType) {
    String className = requestedType.getName() + BINDER_SUFFIX;
    className = className.replace(".", "_");
    return className;
  }

  private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx,
      JClassType requestedType) {
    JPackage pkg = requestedType.getPackage();
    String packageName = pkg == null ? "" : pkg.getName();
    PrintWriter printWriter =
        ctx.tryCreate(logger, packageName, getBinderSimpleName(requestedType));
    if (printWriter == null) {
      return null;
    }

    ClassSourceFileComposerFactory composerFactory =
        new ClassSourceFileComposerFactory(packageName, getBinderSimpleName(requestedType));

    //Add imports
    String[] imports = new String[] {
        SafeHtmlTemplates.class.getName(),
        SafeHtmlTemplates.class.getName() + ".Template",
        SafeHtml.class.getName(),
        HtmlUiBinder.class.getName(),
        HTMLPanel.class.getName(),
        GWT.class.getName(),
        Widget.class.getName(),
        Element.class.getName(),
        DOM.class.getName(),
    };
    
    for (String imp : imports) {
      composerFactory.addImport(imp);
    }
    composerFactory.addImplementedInterface(requestedType.getQualifiedSourceName());

    return composerFactory.createSourceWriter(ctx, printWriter);
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
  
  private Document readDocument(TreeLogger logger, ResourceOracle resourceOracle, JClassType requestedType) throws UnableToCompleteException {
    String templatePath = deduceTemplateFile(logger, requestedType);
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

    return doc;
  }

}
