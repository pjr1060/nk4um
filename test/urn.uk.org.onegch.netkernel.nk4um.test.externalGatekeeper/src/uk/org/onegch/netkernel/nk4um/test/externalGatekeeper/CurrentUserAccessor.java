package uk.org.onegch.netkernel.nk4um.test.externalGatekeeper;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernelroc.mod.layer2.AccessorUtil;
import org.netkernelroc.mod.layer2.Layer2AccessorImpl;

public class CurrentUserAccessor extends Layer2AccessorImpl {

  @Override
  public void onExists(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.createResponseFrom(aContext.exists("session:/currentUser"));
  }

  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.createResponseFrom(aContext.source("session:/currentUser"));
  }
}
