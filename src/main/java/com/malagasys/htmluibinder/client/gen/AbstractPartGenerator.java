package com.malagasys.htmluibinder.client.gen;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.SourceWriter;

abstract class AbstractPartGenerator implements PartGenerator {
  protected final GeneratorContext generatorCtx;
  protected final JClassType requestedType;
  protected final TreeLogger treeLogger;
  protected final SourceWriter srcWriter;

  protected AbstractPartGenerator(GeneratorContext generatorCtx, JClassType requestedType,
      TreeLogger treeLogger, SourceWriter srcWriter) {
    super();
    this.generatorCtx = generatorCtx;
    this.requestedType = requestedType;
    this.treeLogger = treeLogger;
    this.srcWriter = srcWriter;
  }
  
  @Override
  public void generateInnerTypes(SourceWriter srcWriter) throws UnableToCompleteException {
  }

  @Override
  public void generateFields(SourceWriter srcWriter) throws UnableToCompleteException {
  }

  @Override
  public void generateMethods(SourceWriter srcWriter) throws UnableToCompleteException {
  }
}
