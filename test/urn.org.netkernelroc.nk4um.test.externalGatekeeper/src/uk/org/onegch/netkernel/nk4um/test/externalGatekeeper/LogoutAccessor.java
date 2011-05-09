package org.netkernelroc.nk4um.test.externalGatekeeper;

import org.netkernel.http.transport.HttpAccessorImpl;
import org.netkernel.layer0.nkf.INKFRequestContext;

public class LogoutAccessor extends HttpAccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext) throws Exception {
    aContext.delete("session:/currentUser");
  }
}
