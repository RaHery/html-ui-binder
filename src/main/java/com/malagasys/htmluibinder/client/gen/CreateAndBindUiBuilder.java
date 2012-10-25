package com.malagasys.htmluibinder.client.gen;

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
    JParameterizedType requestedItf = (JParameterizedType) context.getRequestedType().getImplementedInterfaces()[0];
    JClassType[] typeArgs = requestedItf.getTypeArgs();
    writer.println("public Widget createAndBindHtml(%s container) {", typeArgs[0].getQualifiedSourceName());
    writer.indent();

    //Create the htmlpanel containing the result
    writer.println("Template t = GWT.create(Template.class);"); 
    writer.println("HTMLPanel panel = new HTMLPanel(t.html());");
    
    //Generate all statements
    context.writeAllStatements();

    writer.println("return panel;");
    
    writer.outdent();
    writer.println("}");
    writer.outdent();
  }
}
