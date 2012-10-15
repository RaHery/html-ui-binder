package com.malagasys.htmluibinder.client.gen;

import java.util.Map;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.SourceWriter;

interface PartGenerator {
  void generate(GeneratorContext generatorCtx, JClassType requestedType,
      TreeLogger treeLogger, SourceWriter srcWriter, Map<String, String> idsMap) throws UnableToCompleteException;
}
