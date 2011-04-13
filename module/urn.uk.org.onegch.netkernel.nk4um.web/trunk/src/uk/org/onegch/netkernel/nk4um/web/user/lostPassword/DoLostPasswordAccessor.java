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

package uk.org.onegch.netkernel.nk4um.web.user.lostPassword;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DoLostPasswordAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    boolean valid= true;
    HDSBuilder reasonsBuilder= new HDSBuilder();
    reasonsBuilder.pushNode("div");
    reasonsBuilder.addNode("p", "Lost password change failed for the following reasons: ");
    reasonsBuilder.pushNode("ul");
    
    if (!aContext.exists("httpRequest:/param/email")) {
      valid= false;
      reasonsBuilder.addNode("li", "Email address must be supplied");
    }
    if (!aContext.exists("httpRequest:/param/new_password")) {
      valid= false;
      reasonsBuilder.addNode("li", "New password must be supplied");
    } else {
      String password= aContext.source("httpRequest:/param/new_password", String.class);
      if (password.trim().length() < 6) {
        valid= false;
        reasonsBuilder.addNode("li", "New password must be 6 characters or longer");
      } else if (!aContext.exists("httpRequest:/param/confirm_password")) {
        valid= false;
        reasonsBuilder.addNode("li", "New password must match confirmation password");
      } else {
        String confirm= aContext.source("httpRequest:/param/confirm_password", String.class);
        if (!password.equals(confirm)) {
          valid= false;
          reasonsBuilder.addNode("li", "New password must match confirmation password");
        } else {}
      }
    }
    
    if (valid && !util.issueExistsRequest("nk4um:db:user:email",
                                          new Arg("email", "httpRequest:/param/email"))){
      valid= false;
      reasonsBuilder.addNode("li", "No account exists with this email address");
    }
    
    if (valid) {
      long uid= util.issueSourceRequest("nk4um:db:user:email",
                                        Long.class,
                                        new Arg("email", "httpRequest:/param/email"));
      
      util.issueSinkRequest("nk4um:db:user:password",
                            null,
                            new ArgByValue("id", uid),
                            new Arg("password", "httpRequest:/param/new_password"));
      
      String activationCode= util.issueNewRequest("nk4um:db:user:activate",
                                                  String.class,
                                                  null,
                                                  new ArgByValue("id", uid));
      
      aContext.sink("session:/message/class", "info");
      aContext.sink("session:/message/title", "Lost password: Activation code sent");
      aContext.sink("session:/message/content", "An activation code has been sent to " + aContext.source("httpRequest:/param/email", String.class));

      HDSBuilder headerBuilder= new HDSBuilder();
      headerBuilder.pushNode("email");
      headerBuilder.addNode("to", aContext.source("httpRequest:/param/email", String.class));
      headerBuilder.addNode("subject", "nk4um Lost Password");
      
      String url= (String) aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url") +
                  "user/doActivate?email=" + aContext.source("httpRequest:/param/email", String.class) +
                  "&code=" + activationCode;
      
      String emailBody= "Hi,\n\n" +
                        "Your new password has been set for nk4um, your account will need re-activating, " +
                        "details provided below:\n\n" +
                        "Username: " + aContext.source("httpRequest:/param/email", String.class) + "\n" +
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
      aContext.sink("session:/message/title", "Lost password");
      aContext.sink("session:/message/content", reasonsBuilder.getRoot());
      aContext.sink("httpResponse:/redirect", "/nk4um/");
    }
  }
}
