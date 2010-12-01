package uk.org.onegch.netkernel.nk4um.web.user.login;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class LoginAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/security/");
    
    String url;
    if (aContext.exists("httpRequest:/header/Referer") &&
        aContext.source("httpRequest:/header/Referer", String.class).contains("/nk4um/")) {
      url= aContext.source("httpRequest:/header/Referer", String.class);
    } else {
      url= "/nk4um/";
    }
    
    aContext.sink("session:/loginRedirect", url);
    
    aContext.createResponseFrom(aContext.source("login.xml"));
  }
}
