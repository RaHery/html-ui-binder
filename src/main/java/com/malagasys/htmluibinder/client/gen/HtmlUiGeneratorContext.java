package com.malagasys.htmluibinder.client.gen;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.user.rebind.SourceWriter;

class HtmlUiGeneratorContext {
  private final Document document;
  private final String resourceClass;
  private final GeneratorContext generatorCtx;
  private final JClassType requestedType;
  private final TreeLogger treeLogger;
  private final SourceWriter srcWriter;
  private final Map<String, String> ids = Maps.newHashMap();
  private final List<String> statements = Lists.newArrayList();
  private final List<String> templateParameters = Lists.newArrayList();

  HtmlUiGeneratorContext(Document document, String resourceClass, GeneratorContext generatorCtx,
      JClassType requestedType, TreeLogger treeLogger, SourceWriter srcWriter) {
    this.document = document;
    this.resourceClass = resourceClass;
    this.generatorCtx = generatorCtx;
    this.requestedType = requestedType;
    this.treeLogger = treeLogger;
    this.srcWriter = srcWriter;
  }

  Document getDocument() {
    return document;
  }

  String getResourceClass() {
    return resourceClass;
  }

  GeneratorContext getGeneratorContext() {
    return generatorCtx;
  }

  JClassType getRequestedType() {
    return requestedType;
  }

  TreeLogger getTreeLogger() {
    return treeLogger;
  }

  SourceWriter getSourceWriter() {
    return srcWriter;
  }

  void putId(String htmlUiId, String id) {
    ids.put(htmlUiId, id);
  }

  String getId(String htmlUiId) {
    return ids.get(htmlUiId);
  }

  void addStatement(String statement) {
    statements.add(statement);
  }

  void writeAllStatements() {
    for (String st : statements) {
      srcWriter.println(st);
    }
  }

  void appendTemplateParameter(String p) {
    templateParameters.add(p);
  }

  int countTemplateParameters() {
    return templateParameters.size();
  }

  String getTemplateParameter(int index) {
    return templateParameters.get(index);
  }
}
