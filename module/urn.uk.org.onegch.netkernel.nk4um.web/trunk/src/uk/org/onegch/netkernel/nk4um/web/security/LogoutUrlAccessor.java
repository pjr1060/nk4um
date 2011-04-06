package uk.org.onegch.netkernel.nk4um.web.security;

import org.netkernel.layer0.nkf.INKFRequestContext;
import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;
import uk.org.onegch.netkernel.nk4um.web.common.UrlGeneratorUtil;

public class LogoutUrlAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.createResponseFrom(UrlGeneratorUtil.lookup(aContext, "//security_logoutUrl", "nk4um:security:doLogout"));
  }
}
