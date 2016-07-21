package com.malagasys.htmluibinder.client.gen;

import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.user.rebind.SourceWriter;

public class CreateAndBindUiBuilder implements PartBuilder {

  @Override
  public void generate(HtmlUiGeneratorContext context) throws UnableToCompleteException {
    SourceWriter writer = context.getSourceWriter();

    writer.println();
    writer.indent();
    JParameterizedType requestedItf =
        (JParameterizedType) context.getRequestedType().getImplementedInterfaces()[0];
    JClassType[] typeArgs = requestedItf.getTypeArgs();
    writer.println("public Widget createAndBindHtml(%s container) {", typeArgs[0]
        .getQualifiedSourceName());
    writer.indent();

    // Create the htmlpanel containing the result
    writer.println("Template t = GWT.create(Template.class);");

    // Inser the resource class if exists
    if (context.getResourceClass() != null) {
      writer.println(context.getResourceClass() + " res = GWT.create(" + context.getResourceClass()
          + ".class);");
    } else if (context.countTemplateParameters() > 0) {
      StringBuilder msg =
          new StringBuilder(
              "No resource class was provided, but the template contains some resources:");
      for (int i = 0; i < context.countTemplateParameters(); ++i) {
        msg.append(context.getTemplateParameter(i));
        msg.append(" ");
      }
      context.getTreeLogger().log(Type.ERROR, msg.toString());
      throw new UnableToCompleteException();
    }

    // Write the method to create the template
    writer.print("HTMLPanel panel = new HTMLPanel(t.html(");
    for (int i = 0; i < context.countTemplateParameters(); ++i) {
      writer.print("res." + context.getTemplateParameter(i) + "()");
      if (i < context.countTemplateParameters() - 1) {
        writer.print(",");
      }
    }
    writer.println("));");

    // Generate all statements
    context.writeAllStatements();

    writer.println("return panel;");

    writer.outdent();
    writer.println("}");
    writer.outdent();
  }
}
