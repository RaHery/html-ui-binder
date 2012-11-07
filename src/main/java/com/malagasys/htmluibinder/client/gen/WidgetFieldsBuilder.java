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
 * For each field annotated with {@link HtmlUiField}, generate a method that create the field. 
 * At the end create a method that assign each field to the builder target.
 * 
 * @author hermann
 */
class WidgetFieldsBuilder implements PartBuilder {

  @Override
  public void generate(HtmlUiGeneratorContext ctx) throws UnableToCompleteException {
    SourceWriter srcWriter = ctx.getSourceWriter();
    final String createMethodName = "__buildWidgets";
    
    //Get the type of the container.
    JParameterizedType requestedItf = (JParameterizedType) ctx.getRequestedType().getImplementedInterfaces()[0];
    JClassType containerType = requestedItf.getTypeArgs()[0];
    List<String> createdMethodNames = new ArrayList<String>();
    
    //Scan visible and annotated fields
    for (JField field : containerType.getFields()) {
      HtmlUiField htmlFieldAnnotation = field.getAnnotation(HtmlUiField.class);
      if (htmlFieldAnnotation != null) {
        if (field.isPrivate()) {
          ctx.getTreeLogger().log(Type.ERROR, "Field `" + field.getName() + "' is annotated " +
          		"with @HtmlUiField but is private.");
          throw new UnableToCompleteException();
        }
        
        String methodName = writeMethodToInitField(field, htmlFieldAnnotation, 
            containerType, ctx);
        createdMethodNames.add(methodName);
      }
    }
    
    //Then create a single method that calls all of the created methods
    srcWriter.println();
    srcWriter.indent();
    srcWriter.println("void %s(%s container, HTMLPanel htmlPanel) {", createMethodName, containerType.getName());
    srcWriter.indent();
    for (String methodName : createdMethodNames) {
      srcWriter.println("%s(container, htmlPanel);", methodName);
    }
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
    
    //Add statement to call the method
    ctx.addStatement(createMethodName + "(container, panel);");
    
  }

  private String writeMethodToInitField(JField field, HtmlUiField annotatedWith, 
      JClassType containerType, HtmlUiGeneratorContext context) throws UnableToCompleteException {
    SourceWriter srcWriter = context.getSourceWriter();
    srcWriter.println();
    
    //This is assured to be unique : the field name is unique within the class.
    String uniqueMethodName = (containerType.getName() + "_" + field.getName()).replace(".", "_").toLowerCase();
    srcWriter.indent();
    srcWriter.println("void %s(%s container, HTMLPanel htmlPanel) {", uniqueMethodName, containerType.getName());
    srcWriter.indent();
    if (!annotatedWith.provided()) {
      srcWriter.println("//Create the field if need be.");
      srcWriter.println("container.%s = GWT.create(%s.class);", field.getName(), field.getType().getQualifiedSourceName());
    }
    
    String htmlUiId = annotatedWith.value();
    if (htmlUiId.equals("")) {
      htmlUiId = field.getName();
    }
    
    if (context.getId(htmlUiId) != null) {
      String id = context.getId(htmlUiId);
      
      //Transfer 'class' and 'style' attributes
      transferCssAttributes(srcWriter, id, field.getName());
      
      //Then replace the element.
      srcWriter.println("htmlPanel.addAndReplaceElement(container.%s, \"%s\");", field.getName(), id);
      srcWriter.outdent();
      srcWriter.println("}");
      srcWriter.outdent();
    } else {
      context.getTreeLogger().log(Type.ERROR, "There is no `htmlui:id' tag with the value `" + htmlUiId + "' found in the html template file.");
      throw new UnableToCompleteException();
    }
    
    return uniqueMethodName;
  }
  
  private void transferCssAttributes(SourceWriter srcWriter, String id, String fieldName) {
    srcWriter.println("Element element = htmlPanel.getElementById(\"%s\");", id);
    srcWriter.println("if (element != null) {");
    srcWriter.indent();
    
    //Transfer the "class" attribute if exist.
    srcWriter.println("if (element.hasAttribute(\"class\")) {");
    srcWriter.indent();
    srcWriter.println("String className = element.getClassName();");
    srcWriter.println("container.%s.addStyleName(className);", fieldName);
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.println();
    
    //Transfer the "style" attribute if exist.
    srcWriter.println("if (element.hasAttribute(\"style\")) {");
    srcWriter.indent();
    srcWriter.println("String style = DOM.getElementAttribute(element, \"style\");");
    srcWriter.println("DOM.setElementAttribute(container.%s.getElement(), \"style\", style);", fieldName);
    srcWriter.outdent();
    srcWriter.println("}");
    
    srcWriter.outdent();
    srcWriter.println("}");
  }
}