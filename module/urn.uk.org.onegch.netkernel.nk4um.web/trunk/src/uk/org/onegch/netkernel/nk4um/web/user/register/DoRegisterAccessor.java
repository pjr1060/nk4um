package uk.org.onegch.netkernel.nk4um.web.user.register;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;
import uk.org.onegch.netkernel.layer2.PrimaryArgByValue;

public class DoRegisterAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    boolean valid= true;
    HDSBuilder reasonsBuilder= new HDSBuilder();
    reasonsBuilder.pushNode("div");
    reasonsBuilder.addNode("p", "Registration failed for the following reasons: ");
    reasonsBuilder.pushNode("ul");
    
    HDSBuilder userDetailsBuilder= new HDSBuilder();
    userDetailsBuilder.pushNode("root");
    
    if (!util.issueSourceRequest("active:recaptcha-verify",
                                 Boolean.class,
                                 new Arg("remoteAddr", "httpRequest:/remote-host"),
                                 new Arg("challenge", "httpRequest:/param/recaptcha_challenge_field"),
                                 new Arg("response", "httpRequest:/param/recaptcha_response_field"))) {
      valid= false;
      reasonsBuilder.addNode("li", "Recaptcha is invalid");
    } else {
      if (!aContext.exists("httpRequest:/param/email")) {
        valid= false;
        reasonsBuilder.addNode("li", "Email address must be supplied");
      } else {
        String emailAddress= aContext.source("httpRequest:/param/email", String.class).trim();
        if (emailAddress.length() == 0) {
          valid= false;
          reasonsBuilder.addNode("li", "Email address must be supplied");
        } else if (!emailAddress.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w+)+$")) {
          valid= false;
          reasonsBuilder.addNode("li", "Email address is not valid");
        } else if (util.issueExistsRequest("nk4um:db:user:email",
                                           new ArgByValue("email", emailAddress))) {
          valid= false;
          reasonsBuilder.addNode("li", "An account already exists with this email address");
        } else {
          userDetailsBuilder.addNode("username", emailAddress);
          userDetailsBuilder.addNode("email", emailAddress);
        }
      }
      if (!aContext.exists("httpRequest:/param/password")) {
        valid= false;
        reasonsBuilder.addNode("li", "Password must be supplied");
      } else {
        String password= aContext.source("httpRequest:/param/password", String.class);
        if (password.trim().length() < 6) {
          valid= false;
          reasonsBuilder.addNode("li", "Password must be 6 characters or longer");
        } else if (!aContext.exists("httpRequest:/param/confirm")) {
          valid= false;
          reasonsBuilder.addNode("li", "Password must match confirmation password");
        } else {
          String confirm= aContext.source("httpRequest:/param/confirm", String.class);
          if (!password.equals(confirm)) {
            valid= false;
            reasonsBuilder.addNode("li", "Password must match confirmation password");
          } else {
            userDetailsBuilder.addNode("password", aContext.source("httpRequest:/param/password"));
          }
        }
      }
      
      if (!aContext.exists("httpRequest:/param/display")) {
        valid= false;
        reasonsBuilder.addNode("li", "Display name must be supplied");
      } else {
        String displayName= aContext.source("httpRequest:/param/display", String.class).trim();
        if (displayName.length() == 0) {
          valid= false;
          reasonsBuilder.addNode("li", "Display name must be supplied");
        } else if (util.issueExistsRequest("nk4um:db:user:displayName",
                                           new ArgByValue("displayName", displayName))) {
          valid= false;
          reasonsBuilder.addNode("li", "An account already exists with this display name");
        } else {
          userDetailsBuilder.addNode("display", aContext.source("httpRequest:/param/display"));
        }
      }
    }
    
    if (valid) {
      util.issueNewRequestAsResponse("nk4um:db:user",
                                     new PrimaryArgByValue(userDetailsBuilder.getRoot()));
      aContext.sink("session:/message/class", "success");
      aContext.sink("session:/message/title", "Registration successful");
      aContext.sink("session:/message/content", "You can now login :-)");
      
      aContext.sink("httpResponse:/redirect", "../");
    } else {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Registration failure");
      aContext.sink("session:/message/content", reasonsBuilder.getRoot());
      
      if (aContext.exists("httpRequest:/params")) {
        aContext.sink("session:/formData/name", "register");
        aContext.sink("session:/formData/params", aContext.source("httpRequest:/params"));
      }
      
      aContext.sink("httpResponse:/redirect", "register");
    }
  }
}
