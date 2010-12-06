package uk.org.onegch.netkernel.nk4um.web.user.activate;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DoActivateAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    onPost(aContext, util);
  }
  
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    boolean valid= true;
    HDSBuilder reasonsBuilder= new HDSBuilder();
    reasonsBuilder.pushNode("div");
    reasonsBuilder.addNode("p", "Activation failed for the following reasons: ");
    reasonsBuilder.pushNode("ul");
    
    if (!aContext.exists("httpRequest:/param/email")) {
      valid= false;
      reasonsBuilder.addNode("li", "Email address must be supplied");
    }
    if (!aContext.exists("httpRequest:/param/code")) {
      valid= false;
      reasonsBuilder.addNode("li", "Activation code must be supplied");
    }
    
    if (valid && !util.issueExistsRequest("nk4um:db:user:activate",
                                          new Arg("email", "httpRequest:/param/email"),
                                          new Arg("activationCode", "httpRequest:/param/code"))){
      valid= false;
      reasonsBuilder.addNode("li", "Email address and activation code do not match");
    }
    
    if (valid) {
      util.issueSourceRequest("nk4um:db:user:activate",
                              null,
                              new Arg("email", "httpRequest:/param/email"),
                              new Arg("activationCode", "httpRequest:/param/code"));
      
      aContext.sink("session:/message/class", "success");
      aContext.sink("session:/message/title", "Account Activated");
      aContext.sink("session:/message/content", "Your account has been activated and you can now login.");
      aContext.sink("httpResponse:/redirect", "login");
    } else {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Activation failure");
      aContext.sink("session:/message/content", reasonsBuilder.getRoot());
      aContext.sink("httpResponse:/redirect", "activate");
    }
  }
}
