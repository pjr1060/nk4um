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

package org.netkernelroc.nk4um.web.user.changePassword;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import org.netkernelroc.mod.layer2.Arg;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.HttpLayer2AccessorImpl;
import org.netkernelroc.mod.layer2.HttpUtil;

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
                                          new Arg("id", "nk4um:security:currentUser"),
                                          new Arg("password", "httpRequest:/param/current_password"),
                                new ArgByValue("siteSalt", aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//site_password_salt")))) {
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
                            new Arg("id", "nk4um:security:currentUser"),
                            new Arg("password", "httpRequest:/param/password"),
                            new ArgByValue("siteSalt", aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//site_password_salt")));
      
      aContext.sink("session:/message/class", "success");
      aContext.sink("session:/message/title", "Password changed");
      aContext.sink("session:/message/content", "Your password has now been changed.");
      aContext.sink("httpResponse:/redirect", "changePassword");
    } else {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Password change failure");
      aContext.sink("session:/message/content", reasonsBuilder.getRoot());
      aContext.sink("httpResponse:/redirect", "changePassword");
    }
  }
}
