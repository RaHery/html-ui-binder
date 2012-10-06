package com.malagasys.htmluibinder.client.gen;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.user.rebind.SourceWriter;

interface PartGenerator {
  void generateInnerTypes(SourceWriter srcWriter) throws UnableToCompleteException;
  void generateFields(SourceWriter srcWriter) throws UnableToCompleteException;
  void generateMethods(SourceWriter srcWriter) throws UnableToCompleteException;
}
