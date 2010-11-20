package uk.org.onegch.netkernel.nk4um.web.security;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DoLoginAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    String url;
    if (aContext.exists("httpRequest:/header/Referer")){
      url= aContext.source("httpRequest:/header/Referer", String.class);
    } else {
      url= "/nk4um/";
    }
    
    if (util.issueExistsRequest("nk4um:db:user:login",
                                new Arg("username", "httpRequest:/param/username"),
                                new Arg("password", "httpRequest:/param/password"))) {
      IHDSNode userId= util.issueSourceRequest("nk4um:db:user:login",
                                               IHDSNode.class,
                                               new Arg("username", "httpRequest:/param/username"),
                                               new Arg("password", "httpRequest:/param/password"));
      
      long id= (Long)userId.getFirstValue("//id");
      
      aContext.sink("session:/currentUser", id);

      aContext.sink("session:/message/class", "success");
      aContext.sink("session:/message/title", "Login Success");
      aContext.sink("session:/message/content", "Your login was successful :-)");
    } else {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Login Failed");
      aContext.sink("session:/message/content", "Invalid email address and/or password.");
    }
    
    aContext.sink("httpResponse:/redirect", url);
  }
}
