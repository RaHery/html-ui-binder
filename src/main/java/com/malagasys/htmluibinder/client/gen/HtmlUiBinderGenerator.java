package com.malagasys.htmluibinder.client.gen;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class HtmlUiBinderGenerator extends Generator {
  private final static String BINDER_SUFFIX = "_HtmlUiBinder";
  
  @Override
  public String generate(TreeLogger logger, GeneratorContext context, String typeName)
      throws UnableToCompleteException {
    //Grab the information about the class requested
    TypeOracle typeOracle = context.getTypeOracle();
    JClassType requestedType = typeOracle.findType(typeName);

    //Check if already generated.
    if (typeOracle.findType(requestedType.getPackage().getName() + "." + getBinderSimpleName(requestedType)) != null) {
      return null;
    }

    logger.log(Type.INFO, "Generating HtmlBinder for type `" + typeName + "'"
        + " Context=" + context);
    //Get a writer to the output class.
    SourceWriter sourceWriter = getSourceWriter(logger, context, requestedType);

    //Source writer is null if the file has already been generated.
    if (sourceWriter != null) {
      sourceWriter.commit(logger);
    }
    
    return requestedType.getPackage().getName() + "." + getBinderSimpleName(requestedType);
  }
  
  private String getBinderSimpleName(JClassType requestedType) {
    String className = requestedType.getName() + BINDER_SUFFIX;
    className = className.replace(".", "_");
    return className;
  }
  
  private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx,
      JClassType requestedType) {
    JPackage pkg = requestedType.getPackage();
    String packageName = pkg == null ? "" : pkg.getName();
    PrintWriter printWriter = ctx.tryCreate(logger, packageName, getBinderSimpleName(requestedType));
    if (printWriter == null) {
      return null;
    }

    ClassSourceFileComposerFactory composerFactory =
        new ClassSourceFileComposerFactory(packageName, getBinderSimpleName(requestedType));

//    String[] imports =
//        new String[] {
//            getProxySupertype().getCanonicalName(), getStreamWriterClass().getCanonicalName(),
//            SerializationStreamWriter.class.getCanonicalName(), GWT.class.getCanonicalName(),
//            ResponseReader.class.getCanonicalName(),
//            SerializationException.class.getCanonicalName(), RpcToken.class.getCanonicalName(),
//            RpcTokenException.class.getCanonicalName(), Impl.class.getCanonicalName(),
//            RpcStatsContext.class.getCanonicalName()};
//    for (String imp : imports) {
//      composerFactory.addImport(imp);
//    }
    composerFactory.addImplementedInterface(requestedType.getQualifiedSourceName());

    return composerFactory.createSourceWriter(ctx, printWriter);
  }
  
  //Extract the content of the html template.
}
