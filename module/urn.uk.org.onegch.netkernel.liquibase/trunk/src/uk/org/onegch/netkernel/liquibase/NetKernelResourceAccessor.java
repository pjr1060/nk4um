package uk.org.onegch.netkernel.liquibase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import liquibase.resource.ClassLoaderResourceAccessor;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.NKFException;
import org.netkernel.layer0.representation.IReadableBinaryStreamRepresentation;

public class NetKernelResourceAccessor extends ClassLoaderResourceAccessor {
  private INKFRequestContext context;
  
  public NetKernelResourceAccessor(INKFRequestContext aContext, ClassLoader cl) {
    super(cl);
    this.context= aContext;
  }
  
  @Override
  public InputStream getResourceAsStream(String arg0) throws IOException {
    InputStream is= super.getResourceAsStream(arg0);
    try {
      if (is == null && context.exists(arg0)) {
        is= context.source(arg0, IReadableBinaryStreamRepresentation.class).getInputStream();
      }
    } catch (NKFException e) {
      throw new IOException(e);
    }
    return is;
  }
  
  @Override
  public Enumeration<URL> getResources(String arg0) throws IOException {
    Enumeration<URL> resources= super.getResources(arg0);
    
    return resources;
  }
  
  @Override
  public ClassLoader toClassLoader() {
    return super.toClassLoader();
  }
}
