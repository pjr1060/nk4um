package uk.org.onegch.netkernel.nk4um.test.externalGatekeeper;

import org.netkernel.layer0.nkf.INKFRequestContext;
import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class CurrentUserAccessor extends Layer2AccessorImpl {

  @Override
  public void onExists(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.createResponseFrom(true);
  }

  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.createResponseFrom("1");
  }
}
