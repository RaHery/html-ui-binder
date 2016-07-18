package com.malagasys.htmluibinder.client.gen;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.user.rebind.SourceWriter;
import com.malagasys.htmluibinder.client.HtmlUiField;

/**
 * For each field annotated with {@link HtmlUiField}, generate a method that create the field. At
 * the end create a method that assign each field to the builder target.
 * 
 * @author hermann
 */

class WidgetFieldsBuilder implements PartBuilder {
  @Override
  public void generate(HtmlUiGeneratorContext ctx) throws UnableToCompleteException {
    SourceWriter srcWriter = ctx.getSourceWriter();
    final String createMethodName = "__buildWidgets";

    // Get the type of the container.
    JParameterizedType requestedItf =
        (JParameterizedType) ctx.getRequestedType().getImplementedInterfaces()[0];
    JClassType containerType = requestedItf.getTypeArgs()[0];
    List<String> createdMethodNames = new ArrayList<String>();

    // create method to transfer node attributes
    writeMethodToTransferNodesAttributes(srcWriter);

    // Scan visible and annotated fields
    for (JField field : containerType.getFields()) {
      HtmlUiField htmlFieldAnnotation = field.getAnnotation(HtmlUiField.class);
      if (htmlFieldAnnotation != null) {
        if (field.isPrivate()) {
          ctx.getTreeLogger()
              .log(
                  Type.ERROR,
                  "Field `" + field.getName() + "' is annotated "
                      + "with @HtmlUiField but is private.");
          throw new UnableToCompleteException();
        }

        String methodName = writeMethodToInitField(field, htmlFieldAnnotation, containerType, ctx);
        createdMethodNames.add(methodName);
      }
    }

    // Then create a single method that calls all of the created methods
    srcWriter.println();
    srcWriter.indent();
    srcWriter.println("void %s(%s container, HTMLPanel htmlPanel) {", createMethodName,
        containerType.getName());
    srcWriter.indent();
    for (String methodName : createdMethodNames) {
      srcWriter.println("%s(container, htmlPanel);", methodName);
    }
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();

    // Add statement to call the method
    ctx.addStatement(createMethodName + "(container, panel);");

  }

  private String writeMethodToInitField(JField field, HtmlUiField annotatedWith,
      JClassType containerType, HtmlUiGeneratorContext context) throws UnableToCompleteException {
    SourceWriter srcWriter = context.getSourceWriter();
    srcWriter.println();

    // This is assured to be unique : the field name is unique within the class.
    String uniqueMethodName =
        (containerType.getName() + "_" + field.getName()).replace(".", "_").toLowerCase();
    srcWriter.indent();
    srcWriter.println("void %s(%s container, HTMLPanel htmlPanel) {", uniqueMethodName,
        containerType.getName());
    srcWriter.indent();
    if (!annotatedWith.provided()) {
      srcWriter.println("//Create the field if need be.");
      srcWriter.println("container.%s = GWT.create(%s.class);", field.getName(), field.getType()
          .getQualifiedSourceName());
    }

    String htmlUiId = annotatedWith.value();
    if (htmlUiId.equals("")) {
      htmlUiId = field.getName();
    }

    if (context.getId(htmlUiId) != null) {
      String id = context.getId(htmlUiId);

      // Transfer some node attributes
      srcWriter.println(
          "transferNodesAttributes(htmlPanel.getElementById(\"%s\"), container.%s.getElement());",
          id, field.getName());

      // Then replace the element.
      srcWriter.println("htmlPanel.addAndReplaceElement(container.%s, \"%s\");", field.getName(),
          id);
      srcWriter.outdent();
      srcWriter.println("}");
      srcWriter.outdent();
    } else {
      context.getTreeLogger().log(
          Type.ERROR,
          "There is no `htmlui:id' tag with the value `" + htmlUiId
              + "' found in the html template file.");
      throw new UnableToCompleteException();
    }

    return uniqueMethodName;
  }

  private void writeMethodToTransferNodesAttributes(SourceWriter srcWriter) {
    srcWriter.indent();
    srcWriter.println("void transferNodesAttributes(Element source, Element target) {");
    srcWriter.indent();
    srcWriter.println("if (source != null) {");
    srcWriter.indent();

    srcWriter.println("for(String attrName : Constants.TRANSFERABLE_ATTRIBUTES) {");
    srcWriter.indent();
    srcWriter.println("if (source.hasAttribute(attrName)) {");
    srcWriter.indent();
    srcWriter.println("String attrValue = source.getAttribute(attrName);");
    srcWriter.println("if ((attrValue != null) && (!attrValue.trim().isEmpty())) {");
    srcWriter.indent();
    srcWriter.println("target.setAttribute(attrName, attrValue);");
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
    srcWriter.println("}");

    srcWriter.outdent();
    srcWriter.println("}");

    // Transfer data-* attributes
    srcWriter.println("ElementExt e = source.cast();");
    srcWriter.println("JsArray<ElementExt.Attr> attributes = e.attributes();");
    srcWriter.println("for (int i = 0; i < attributes.length(); ++i) {");
    srcWriter.indent();
    srcWriter.println("if (attributes.get(i).name().startsWith(\"data-\")) {");
    srcWriter.indent();
    srcWriter.println("target.setAttribute(attributes.get(i).name(), attributes.get(i).value());");
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
  }
}
