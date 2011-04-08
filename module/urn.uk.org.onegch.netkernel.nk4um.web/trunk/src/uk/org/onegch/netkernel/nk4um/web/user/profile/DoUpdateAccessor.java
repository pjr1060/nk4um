package uk.org.onegch.netkernel.nk4um.web.user.profile;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;
import uk.org.onegch.netkernel.layer2.PrimaryArgByValue;

public class DoUpdateAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    IHDSNode currentUserDetails= util.issueSourceRequest("nk4um:db:user",
                                                         IHDSNode.class,
                                                         new Arg("id", "nk4um:security:currentUser"));
    
    boolean valid= true;
    boolean emailChanged= false;
    
    HDSBuilder reasonsBuilder= new HDSBuilder();
    reasonsBuilder.pushNode("div");
    reasonsBuilder.addNode("p", "Profile update failed for the following reasons: ");
    reasonsBuilder.pushNode("ul");
    
    HDSBuilder userDetailsBuilder= new HDSBuilder();
    userDetailsBuilder.pushNode("root");

    HDSBuilder userMetaDetailsBuilder= new HDSBuilder();
    userMetaDetailsBuilder.pushNode("root");

    IHDSNode pdsState = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);
    boolean builtinModel= (pdsState.getFirstValue("//security_external") == null);

    // Only update email address if we're using the builtin authentication model
    if (builtinModel) {
      if (!aContext.exists("httpRequest:/param/email")) {
        valid= false;
        reasonsBuilder.addNode("li", "Email address must be supplied");
      } else {
        String emailAddress= aContext.source("httpRequest:/param/email", String.class).trim();
        if (emailAddress.length() == 0) {
          valid= false;
          reasonsBuilder.addNode("li", "Email address must be supplied");
        } else if (emailAddress.equals(currentUserDetails.getFirstValue("//email"))) {
          // it hasn't changed so use existing value
          userDetailsBuilder.addNode("username", currentUserDetails.getFirstValue("//username"));
          userDetailsBuilder.addNode("email", currentUserDetails.getFirstValue("//email"));
        } else if (!emailAddress.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w+)+$")) {
          valid= false;
          reasonsBuilder.addNode("li", "Email address is not valid");
        } else if (util.issueExistsRequest("nk4um:db:user:email",
                                           new ArgByValue("email", emailAddress))) {
          valid= false;
          reasonsBuilder.addNode("li", "An account already exists with this email address");
        } else {
          emailChanged= true;
          userDetailsBuilder.addNode("username", emailAddress);
          userDetailsBuilder.addNode("email", emailAddress);
        }
      }
    }
    
    if (!aContext.exists("httpRequest:/param/display_name")) {
      valid= false;
      reasonsBuilder.addNode("li", "Display name must be supplied");
    } else {
      String displayName= aContext.source("httpRequest:/param/display_name", String.class).trim();
      if (displayName.length() == 0) {
        valid= false;
        reasonsBuilder.addNode("li", "Display name must be supplied");
      } else if (displayName.equals(currentUserDetails.getFirstValue("//display_name"))) {
        // it hasn't changed so use existing value
        userMetaDetailsBuilder.addNode("display_name", currentUserDetails.getFirstValue("//display_name"));
      } else if (util.issueExistsRequest("nk4um:db:user:displayName",
                                         new ArgByValue("displayName", displayName))) {
        valid= false;
        reasonsBuilder.addNode("li", "An account already exists with this display name");
      } else {
        userMetaDetailsBuilder.addNode("display_name", aContext.source("httpRequest:/param/display_name"));
      }
    }
    
    if (valid) {
      // SINK new user details
      if (builtinModel) {
        util.issueSinkRequest("nk4um:db:user",
                              new PrimaryArgByValue(userDetailsBuilder.getRoot()),
                              new Arg("id", "nk4um:security:currentUser"));
      }
      util.issueSinkRequest("nk4um:db:user:meta",
                              new PrimaryArgByValue(userMetaDetailsBuilder.getRoot()),
                              new Arg("id", "nk4um:security:currentUser"));
      
      if (builtinModel && emailChanged) {
        // create new activation code
        String activationCode= util.issueNewRequest("nk4um:db:user:activate",
                                                    String.class,
                                                    null,
                                                    new Arg("id", "nk4um:security:currentUser"));
        
        // email new activation code
        HDSBuilder headerBuilder= new HDSBuilder();
        headerBuilder.pushNode("email");
        headerBuilder.addNode("to", userDetailsBuilder.getRoot().getFirstValue("//email"));
        headerBuilder.addNode("subject", "nk4um Registration");
        
        String url= aContext.source("httpRequest:/url", String.class);
        url= url.substring(0, url.indexOf("/doUpdateProfile")) +
                   "/doActivate?email=" + userDetailsBuilder.getRoot().getFirstValue("//email") +
                   "&code=" + activationCode;
        
        String emailBody= "Dear " + aContext.source("httpRequest:/param/display", String.class) + ",\n\n" +
                          "Thank you for registering on nk4um\n\n" +
                          "Username: " + userDetailsBuilder.getRoot().getFirstValue("//email") + "\n" +
                          "Password: Not shown for security reasons\n\n" +
                          "Activation code: " + activationCode + "\n" +
                          "Activation URL: " + url;
        
        try {
          util.issueSourceRequest("active:sendmail",
                                  null,
                                  new ArgByValue("header", headerBuilder.getRoot()),
                                  new ArgByValue("body", emailBody));
        } catch (Exception e) {}
        
        aContext.sink("session:/message/class", "info");
        aContext.sink("session:/message/title", "Update Profile: Activation code sent");
        aContext.sink("session:/message/content", "An activation code has been sent to " + userDetailsBuilder.getRoot().getFirstValue("//email"));
        aContext.sink("httpResponse:/redirect", "activate");
      } else {
        aContext.sink("session:/message/class", "success");
        aContext.sink("session:/message/title", "Update Profile: success");
        aContext.sink("session:/message/content", "Your account has been successfully updated");
        aContext.sink("httpResponse:/redirect", "profile");
      }
    } else {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Update Profile failure");
      aContext.sink("session:/message/content", reasonsBuilder.getRoot());
      
      if (aContext.exists("httpRequest:/params")) {
        aContext.sink("session:/formData/name", "profile");
        aContext.sink("session:/formData/params", aContext.source("httpRequest:/params"));
      }
      
      aContext.sink("httpResponse:/redirect", "profile");
    }
  }
}
