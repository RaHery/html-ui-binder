package com.malagasys.htmluibinder.client.gen;

import java.util.List;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.user.rebind.SourceWriter;
import com.malagasys.htmluibinder.client.HtmlUiField;
import com.malagasys.htmluibinder.client.HtmlUiHandler;

/**
 * Generator of event handling implementation.
 * 
 * @author hermann
 */
public class EventHandlerBuilder implements PartBuilder {

  @Override
  public void generate(HtmlUiGeneratorContext context) throws UnableToCompleteException {
    TreeLogger logger = context.getTreeLogger();
    SourceWriter srcWriter = context.getSourceWriter();
    //Writer header of the method
    srcWriter.println();
    srcWriter.indent();
    
    JParameterizedType requestedItf = (JParameterizedType) context.getRequestedType().getImplementedInterfaces()[0];
    JClassType containerType = requestedItf.getTypeArgs()[0];
    srcWriter.println("void __createEventHandlers(final %s container) {", containerType.getName());
    srcWriter.indent();
    
    JMethod[] methods = containerType.getMethods();
    for (JMethod method : methods) {
      HtmlUiHandler annotation = method.getAnnotation(HtmlUiHandler.class);
      if(annotation != null) {
        logger.log(Type.DEBUG, "Annotated method with @HtmlUiHandler found:" + method.getName());
        
        //Check the visibility of the method
        if (method.isPrivate()) {
          logger.log(Type.ERROR, "Method annotated with @HtmlUiHandler should not be private : " +
          		"`" + method.getName() + "'");
          throw new UnableToCompleteException();
        }
        
        writeEventHandlerMethod(context, annotation.value(), method);
      }
    }
    
    //Close method
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
    context.addStatement("__createEventHandlers(container);");
  }
  
  private void writeEventHandlerMethod(HtmlUiGeneratorContext context, String fieldName, JMethod targetMethod)
    throws UnableToCompleteException {
    TreeLogger logger = context.getTreeLogger();
    logger.log(Type.DEBUG, "Writing event handler calling method `" + targetMethod.getName() + "'");
    
    //First, find field having the same id
    JField field = findFieldWithName(targetMethod.getEnclosingType(), fieldName);
    if (field == null) {
      logger.log(Type.ERROR, "The method `" + targetMethod.getName() + "' is annotated with" +
      		" @HtmlUiHandler(\"" + fieldName + "\") but there is no field with the name" +
      				" `" + fieldName + "'");
      throw new UnableToCompleteException();
    }
    
    //Now find the handler event type
    JClassType eventType = findEventType(logger, targetMethod, field);
    JMethod[] methods = findEventListenerMethod(logger, field, eventType);
    writeEventHandlerImplementation(context, field.getName(), targetMethod, methods[0], methods[1], eventType);
  }

  private void writeEventHandlerImplementation(HtmlUiGeneratorContext context, String fieldName, 
      JMethod targetMethod, JMethod addHandlerMethod, JMethod handlerMethod, JClassType eventType) throws UnableToCompleteException {
    SourceWriter srcWriter = context.getSourceWriter();
    srcWriter.println("container.%s.%s(new %s(){", fieldName, addHandlerMethod.getName(), 
        handlerMethod.getEnclosingType().getQualifiedSourceName());
    srcWriter.indent();
    //TODO in case of an event handler having multiple methods, add
    //empty implementation for the other methods.
    srcWriter.println("public void %s(%s event) {", handlerMethod.getName(), eventType.getQualifiedSourceName());
    srcWriter.indent();
    srcWriter.println("container.%s(event);", targetMethod.getName());
    srcWriter.outdent();
    srcWriter.println("}");
    srcWriter.outdent();
    srcWriter.println("});");
    srcWriter.println();
  }
  
  private JMethod[] findEventListenerMethod(TreeLogger logger, JField field, JClassType eventType) throws UnableToCompleteException {
    TreeLogger debugLogger = logger.branch(Type.DEBUG, "Find event listener method for the field `" + field.getName() + "'" +
    		"; EventType is `" + eventType + "'");
    JClassType fieldType = (JClassType) field.getType();
    JMethod[] methods = null;
    
    for (JMethod method : fieldType.getInheritableMethods()) {
      //Check method which name start with 'add...()' and having a single parameter
      if (method.getName().startsWith("add") && !method.isPrivate()) {
        JType[] parameterTypes = method.getParameterTypes();
        if (parameterTypes != null && parameterTypes.length == 1 
            && parameterTypes[0].isInterface() != null) {
          JClassType eventHandler = parameterTypes[0].isInterface();

          //Now check that this eventHandler has a method accepting the eventype as parameter
          //If there are more than a single method accepting a single parameter of type eventType
          //==> ambiguous so raise an exception.
          List<JMethod> matchingMethods = Lists.newArrayList();
          for (JMethod eventHandlerMethod : eventHandler.getMethods()) {
            JType[] types = eventHandlerMethod.getParameterTypes();
            if (types != null && types.length == 1 && types[0].equals(eventType)) {
              debugLogger.log(Type.DEBUG, "Found a method matching the eventType : `" + eventHandlerMethod.getName() + "'");
              matchingMethods.add(eventHandlerMethod);
            }
          }

          if (matchingMethods.size() > 1) {
            logger.log(Type.ERROR, "Ambiguous. There are " + matchingMethods.size() + " methods in the event handler interface "
                + "`" + eventHandler.getName() + "' accepting a parameter of type `" + eventType.getName() + "'");
            throw new UnableToCompleteException();
          }
          
          if (matchingMethods.size() == 1) {
            methods = new JMethod[]{method, matchingMethods.get(0)};
            break;
          }
        }
      }
    }

    if (methods == null) {
      logger.log(Type.ERROR, "There is no eventhandler to handle event of type `" + eventType.getName() + "'" +
      		" that can be added to field `" + field.getName() + "(" + field.getType() + ")'");
      throw new UnableToCompleteException();
    }

    return methods;
  }
  
  private JClassType findEventType(TreeLogger logger, JMethod handlerMethod, JField ownerField) 
      throws UnableToCompleteException {
    JType[] types = handlerMethod.getParameterTypes();
    if (types == null || types.length == 0) {
      logger.log(Type.ERROR, "Ambiguous handler method : `" + handlerMethod + "'. Expected 1 parameter" +
      		" but found " + ((types == null) ? 0 : types.length));
      throw new UnableToCompleteException();
    }
    
    //TODO : check if event is a subclass of Event ???
    JClassType eventType = types[0].isClass();
    if (eventType == null) {
      logger.log(Type.ERROR, "Invalid event type `" + types[0]);
      throw new UnableToCompleteException();
    }
    
    return eventType;
  }
  
  private JField findFieldWithName(JClassType requestedType, String name) {
    JField field = null;
    
    //Check the annotated name first
    for (JField f : requestedType.getFields()) {
      if (!f.isPrivate()) {
        HtmlUiField annotation = f.getAnnotation(HtmlUiField.class);
        if (annotation != null) {
          if (annotation.value().equals(name)) {
            field = f;
            break;
          }
        }
      }
    }
    
    //Check the field name
    if (field == null) {
      for (JField f : requestedType.getFields()) {
        if (!f.isPrivate()) {
          HtmlUiField annotation = f.getAnnotation(HtmlUiField.class);
          if (annotation != null) {
            if (f.getName().equals(name)) {
              field = f;
              break;
            }
          }
        }
      }
    }
    
    return field;
  }
}

