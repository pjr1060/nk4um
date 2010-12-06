package uk.org.onegch.netkernel.nk4um.admin.configuration;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class DoEditAccessor extends Layer2AccessorImpl
{
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.sink("pds:/nk4um/config.xml", aContext.source("httpRequest:/params"));
    
    aContext.sink("httpResponse:/redirect", "edit");
  }
}
