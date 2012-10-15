package com.malagasys.htmluibinder.client.gen;

import java.io.PrintWriter;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.malagasys.htmluibinder.client.HtmlUiBinder;

public class HtmlUiBinderGenerator extends Generator {
  private final static String BINDER_SUFFIX = "_HtmlUiBinder";

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

      PartGenerator[] generators = new PartGenerator[] {
          new SafeHtmlTemplateGenerator(),
          new WidgetFieldsGenerator(),
      };
      
      //The generate method
      Map<String, String> idsMap = Maps.newHashMap();
      for (PartGenerator g : generators) {
        g.generate(context, requestedType, logger, sourceWriter, idsMap);
      }

      //Finally, generate the createAndBindHtml()
      writeCreateAndBindUiImpl(logger, sourceWriter, requestedType);
      
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
    };
    for (String imp : imports) {
      composerFactory.addImport(imp);
    }
    composerFactory.addImplementedInterface(requestedType.getQualifiedSourceName());

    return composerFactory.createSourceWriter(ctx, printWriter);
  }

  private void writeCreateAndBindUiImpl(TreeLogger logger, SourceWriter writer, JClassType requestedType) {
    writer.println();
    writer.indent();
    JParameterizedType requestedItf = (JParameterizedType) requestedType.getImplementedInterfaces()[0];
    JClassType[] typeArgs = requestedItf.getTypeArgs();
    writer.println("public Widget createAndBindHtml(%s container) {", typeArgs[0].getQualifiedSourceName());
    writer.indent();

    //Create the htmlpanel containing the result
    writer.println("Template t = GWT.create(Template.class);"); 
    writer.println("HTMLPanel p = new HTMLPanel(t.html());");
    
    //Call the method generating the fields
    writer.println("buildWidgets(container, p);");
    writer.println("return p;");
    
    writer.outdent();
    writer.println("}");
    writer.outdent();
  }
}
