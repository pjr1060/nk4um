package uk.org.onegch.netkernel.nk4um.test.externalGatekeeper;

import org.netkernel.http.transport.HttpAccessorImpl;
import org.netkernel.layer0.nkf.INKFRequestContext;

public class LoginAccessor extends HttpAccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext) throws Exception {
    aContext.sink("session:/currentUser", "2");
  }
}
