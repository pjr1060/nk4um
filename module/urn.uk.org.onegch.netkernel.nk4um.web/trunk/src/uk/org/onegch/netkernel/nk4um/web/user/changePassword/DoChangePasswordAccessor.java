package uk.org.onegch.netkernel.nk4um.web.user.changePassword;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DoChangePasswordAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    boolean valid= true;
    HDSBuilder reasonsBuilder= new HDSBuilder();
    reasonsBuilder.pushNode("div");
    reasonsBuilder.addNode("p", "Password change failed for the following reasons: ");
    reasonsBuilder.pushNode("ul");
    
    if (!aContext.exists("httpRequest:/param/current_password")) {
      valid= false;
      reasonsBuilder.addNode("li", "Current password must be supplied");
    } else {
      // check password
    }
    
    if (valid && !util.issueExistsRequest("nk4um:db:user:password",
                                          new Arg("id", "session:/currentUser"),
                                          new Arg("password", "httpRequest:/param/current_password"))) {
      valid= false;
      reasonsBuilder.addNode("li", "Current password is incorrect");
    }
    

    if (valid){
      if (!aContext.exists("httpRequest:/param/password")) {
        valid= false;
        reasonsBuilder.addNode("li", "Password must be supplied");
      } else {
        String password= aContext.source("httpRequest:/param/password", String.class);
        if (password.trim().length() < 6) {
          valid= false;
          reasonsBuilder.addNode("li", "Password must be 6 characters or longer");
        } else if (!aContext.exists("httpRequest:/param/confirm_password")) {
          valid= false;
          reasonsBuilder.addNode("li", "Password must match confirmation password");
        } else {
          String confirm= aContext.source("httpRequest:/param/confirm_password", String.class);
          if (!password.equals(confirm)) {
            valid= false;
            reasonsBuilder.addNode("li", "Password must match confirmation password");
          } else {}
        }
      }
    }
    
    if (valid) {
      util.issueSinkRequest("nk4um:db:user:password",
                            null,
                            new Arg("id", "session:/currentUser"),
                            new Arg("password", "httpRequest:/param/password"));
      
      aContext.sink("session:/message/class", "success");
      aContext.sink("session:/message/title", "Password changed");
      aContext.sink("session:/message/content", "Your password has now been changed.");
      aContext.sink("httpResponse:/redirect", "activate");
    } else {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Password change failure");
      aContext.sink("session:/message/content", reasonsBuilder.getRoot());
      aContext.sink("httpResponse:/redirect", "lostPassword");
    }
  }
}
