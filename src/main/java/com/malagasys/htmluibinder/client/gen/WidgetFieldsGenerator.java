package com.malagasys.htmluibinder.client.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.user.rebind.SourceWriter;
import com.malagasys.htmluibinder.client.HtmlUiField;

/**
 * For each, generate a method that create the field. At the end create a method
 * that assign each field to the builder target.
 * 
 * @author hermann
 */
class WidgetFieldsGenerator implements PartGenerator {

  @Override
  public void generate(GeneratorContext generatorCtx, JClassType requestedType,
      TreeLogger treeLogger, SourceWriter srcWriter, Map<String, String> idsMap) throws UnableToCompleteException {
    //Get the type of the container.
    JParameterizedType requestedItf = (JParameterizedType) requestedType.getImplementedInterfaces()[0];
    JClassType containerType = requestedItf.getTypeArgs()[0];
    List<String> createdMethodNames = new ArrayList<String>();
    
    //Scan visible and annotated fields
    for (JField field : containerType.getFields()) {
      HtmlUiField htmlFieldAnnotation = field.getAnnotation(HtmlUiField.class);
      if (htmlFieldAnnotation != null && (field.isPublic() || field.isDefaultAccess() || field.isProtected())) {
        String methodName = writeMethodToCreateField(treeLogger, srcWriter, field, htmlFieldAnnotation, 
            containerType, idsMap);
        createdMethodNames.add(methodName);
      }
    }
    
    //Then create a single method that calls all of the created method
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

  private String writeMethodToCreateField(TreeLogger treeLogger, SourceWriter srcWriter, JField field, HtmlUiField annotatedWith, 
      JClassType containerType, Map<String, String> idsMap) throws UnableToCompleteException {
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
    
    if (idsMap.containsKey(htmlUiId)) {
      srcWriter.println("htmlPanel.addAndReplaceElement(container.%s, \"%s\");", field.getName(), idsMap.get(htmlUiId));
      srcWriter.outdent();
      srcWriter.println("}");
      srcWriter.outdent();
    } else {
      treeLogger.log(Type.ERROR, "There is no `htmlui:id' tag with the value `" + htmlUiId + "' found in the html template file.");
      throw new UnableToCompleteException();
    }
    
    return uniqueMethodName;
  }
}
