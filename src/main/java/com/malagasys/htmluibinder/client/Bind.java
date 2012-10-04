package com.malagasys.htmluibinder.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind some method with an element in the template html.
 * 
 * @author hermann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Bind {
  
  /**
   * The name of the id of the element to be replaced in the html.
   * @return
   */
  String id() default "String";
}
