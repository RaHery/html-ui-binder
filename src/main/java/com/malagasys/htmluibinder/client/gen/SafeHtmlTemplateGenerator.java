package com.malagasys.htmluibinder.client.gen;

import java.io.IOException;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.resource.ResourceOracle;
import com.google.gwt.dev.util.Util;
import com.google.gwt.user.rebind.SourceWriter;
import com.malagasys.htmluibinder.client.HtmlUiTemplate;

class SafeHtmlTemplateGenerator extends AbstractPartGenerator {
  private final static String TEMPLATE_SUFFIX = ".html";
  
  SafeHtmlTemplateGenerator(GeneratorContext generatorCtx, JClassType requestedType,
      TreeLogger treeLogger, SourceWriter srcWriter) {
    super(generatorCtx, requestedType, treeLogger, srcWriter);
  }

  @Override
  public void generateInnerTypes(SourceWriter srcWriter) throws UnableToCompleteException {
    writeSafeHtmlTemplate(treeLogger, this.srcWriter, requestedType, generatorCtx.getResourcesOracle());
  }

  private void writeSafeHtmlTemplate(TreeLogger logger, SourceWriter writer,
      JClassType requestedType, ResourceOracle resourceOracle) throws UnableToCompleteException {
    String templateFile = deduceTemplateFile(logger, requestedType);
    String templateContent = readTemplateFileContent(logger, resourceOracle, templateFile);

    // normalize the templatecontent first
    templateContent = replaceQuote(toSingleLine(templateContent));

    writer.indent();
    writer.println("interface Template extends SafeHtmlTemplates{");
    writer.indentln("@Template(\"" + templateContent + "\")");
    writer.println("SafeHtml html();");
    writer.outdent();
    writer.println("}");
    writer.outdent();
  }

  private String readTemplateFileContent(TreeLogger logger, ResourceOracle resourceOracle,
      String templatePath) throws UnableToCompleteException {
    Resource resource = resourceOracle.getResourceMap().get(templatePath);
    if (null == resource) {
      logger.log(Type.ERROR, "Unable to find resource: " + templatePath);
    }

    try {
      String content = Util.readStreamAsString(resource.openContents());
      return content;
    } catch (IOException e) {
      logger.log(Type.ERROR, "Unable to read template file: " + templatePath, e);
      throw new UnableToCompleteException();
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

  private static String toSingleLine(String s) {
    String result = s.replace(System.getProperty("line.separator"), "");
    return result;
  }

  private static String replaceQuote(String s) {
    String result = s.replace("\"", "'");
    return result;
  }
}
