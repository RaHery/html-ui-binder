package com.malagasys.htmluibinder.client.gen;

import java.util.Map;

import org.w3c.dom.Document;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.user.rebind.SourceWriter;

class HtmlUiGeneratorContext {
  private final Document document;
  private final GeneratorContext generatorCtx;
  private final JClassType requestedType;
  private final TreeLogger treeLogger;
  private final SourceWriter srcWriter;
  private final Map<String, String> ids = Maps.newHashMap();

  HtmlUiGeneratorContext(Document document, GeneratorContext generatorCtx,
      JClassType requestedType, TreeLogger treeLogger, SourceWriter srcWriter) {
    this.document = document;
    this.generatorCtx = generatorCtx;
    this.requestedType = requestedType;
    this.treeLogger = treeLogger;
    this.srcWriter = srcWriter;
  }

  Document getDocument() {
    return document;
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
}
