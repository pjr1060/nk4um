/*
 * Copyright (C) 2010-2011 by Chris Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.org.onegch.netkernel.nk4um.web.user.register;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
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
      long uid= util.issueNewRequest("nk4um:db:user",
                                     Long.class,
                                     new PrimaryArgByValue(userDetailsBuilder.getRoot()));
      
      String activationCode= util.issueNewRequest("nk4um:db:user:activate",
                                                  String.class,
                                                  null,
                                                  new ArgByValue("id", uid));
      
      aContext.sink("session:/message/class", "info");
      aContext.sink("session:/message/title", "Registration: Activation code sent");
      aContext.sink("session:/message/content", "An activation code has been sent to " + userDetailsBuilder.getRoot().getFirstValue("//email"));
      
      HDSBuilder headerBuilder= new HDSBuilder();
      headerBuilder.pushNode("email");
      headerBuilder.addNode("to", userDetailsBuilder.getRoot().getFirstValue("//email"));
      headerBuilder.addNode("subject", "nk4um Registration");
      
      String url= (String) aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url") +
                 "user/doActivate?email=" + userDetailsBuilder.getRoot().getFirstValue("//email") +
                 "&code=" + activationCode;
      
      String emailBody= "Dear " + aContext.source("httpRequest:/param/display", String.class) + ",\n\n" +
                        "Thank you for registering on nk4um\n\n" +
                        "Username: " + userDetailsBuilder.getRoot().getFirstValue("//email") + "\n" +
                        "Password: Not shown for security reasons\n\n" +
                        "Activation code: " + activationCode + "\n" +
                        "Activation URL: " + url;
      
      util.issueSourceRequest("active:sendmail",
                              null,
                              new ArgByValue("header", headerBuilder.getRoot()),
                              new ArgByValue("body", emailBody));
      
      aContext.sink("httpResponse:/redirect", "activate");
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
