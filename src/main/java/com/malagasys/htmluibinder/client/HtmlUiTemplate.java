package com.malagasys.htmluibinder.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating the template file to be used.
 * 
 * @author hermann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HtmlUiTemplate {
  /**
   * The file name containing the html template.
   *
   * If the name provided is empty, the default value is the name same name as the target class.
   * 
   * @return
   */
  String value() default "";

  /**
   * Resource used to find resource-based string.
   * 
   * @return
   */
  String resourceClass() default "";
}
