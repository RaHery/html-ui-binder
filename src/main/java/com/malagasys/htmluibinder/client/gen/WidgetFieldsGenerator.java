package com.malagasys.htmluibinder.client.gen;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.rebind.SourceWriter;
import com.malagasys.htmluibinder.client.HtmlUiField;

/**
 * For each field annotated with {@link HtmlUiField}, generate a method that create the field. 
 * At the end create a method that assign each field to the builder target.
 * 
 * @author hermann
 */
class WidgetFieldsGenerator implements PartGenerator {

  @Override
  public void generate(HtmlUiGeneratorContext ctx) throws UnableToCompleteException {
    SourceWriter srcWriter = ctx.getSourceWriter();
    
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
    srcWriter.println("void buildWidgets(%s container, HTMLPanel htmlPanel) {", containerType.getName());
    srcWriter.indent();
    for (String methodName : createdMethodNames) {
      srcWriter.println("%s(container, htmlPanel);", methodName);
    }
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
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
    srcWriter.println("//Inject the field into the html panel.");
    
    String htmlUiId = annotatedWith.value();
    if (htmlUiId.equals("")) {
      htmlUiId = field.getName();
    }
    
    if (context.getId(htmlUiId) != null) {
      String id = context.getId(htmlUiId);
      srcWriter.println("htmlPanel.addAndReplaceElement(container.%s, \"%s\");", field.getName(), id);
      
      //TODO : Transfer all of the attributes or only some attributes? 
      Element element = context.getDocument().getElementById(id);
      srcWriter.println("container.%s.addStyleName(\"%s\");", 
          field.getName(), element.getAttribute("class"));
      String style = element.getAttribute("style");
      if (style != null && style.length() > 0) {
        srcWriter.println("DOM.setElementProperty(container.%s.getElement(), \"%s\", \"%s\")",
            field.getName(), "style", style);
      }
      
      srcWriter.outdent();
      srcWriter.println("}");
      srcWriter.outdent();
    } else {
      context.getTreeLogger().log(Type.ERROR, "There is no `htmlui:id' tag with the value `" + htmlUiId + "' found in the html template file.");
      throw new UnableToCompleteException();
    }
    
    return uniqueMethodName;
  }
}
