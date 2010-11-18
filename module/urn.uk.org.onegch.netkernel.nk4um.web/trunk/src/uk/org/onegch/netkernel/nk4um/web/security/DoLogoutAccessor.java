package uk.org.onegch.netkernel.nk4um.web.security;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DoLogoutAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    String url;
    if (aContext.exists("httpRequest:/header/Referer")){
      url= aContext.source("httpRequest:/header/Referer", String.class);
    } else {
      url= "/nk4um/";
    }
    
    aContext.delete("session:/currentUser");

    aContext.sink("session:/message/info", "success");
    aContext.sink("session:/message/title", "Logout Success");
    aContext.sink("session:/message/content", "You have successful logged out :-)");
    
    aContext.sink("httpResponse:/redirect", url);
  }
}
