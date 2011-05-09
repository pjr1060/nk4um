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

package uk.org.onegch.netkernel.nk4um.web.security;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFRequestReadOnly;
import org.netkernel.layer0.nkf.NKFException;

import org.netkernel.layer0.representation.IHDSNode;
import org.netkernelroc.mod.layer2.AccessorUtil;
import org.netkernelroc.mod.layer2.Arg;
import org.netkernelroc.mod.layer2.ArgByRequest;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.Layer2AccessorImpl;

public class AccessControlAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    INKFRequestReadOnly request= aContext.source("arg:request", INKFRequestReadOnly.class);
    String name= request.getResolvedElementId();
    
    // check maintenance mode
    if (!name.startsWith("nk4um:style") &&
        aContext.source("nk4um:db:liquibase:updateAvailable", Boolean.class)) {
      maintenanceMode(aContext, util);
    } else {
      INKFRequest processIncludesReq= util.createSourceRequest("active:xrl2",
                                                               null,
                                                               new Arg("template", "res:/uk/org/onegch/netkernel/nk4um/web/mapperConfig.xml"));
      
      String requiredRole= util.issueSourceRequest("active:xpath2",
                                                   String.class,
                                                   new ArgByRequest("operand", processIncludesReq),
                                                   new ArgByValue("operator", "xs:string(//endpoint[id='" + name +"']/role)"));
      
      if (requiredRole == null) {
        allowRequest(request, aContext);
      } else if (requiredRole.equalsIgnoreCase("User") && aContext.exists("nk4um:security:currentUser")) {
        allowRequest(request, aContext);
      } else {
        denyRequest(aContext);
      }
    }
  }
  
  private void allowRequest(INKFRequestReadOnly request, INKFRequestContext aContext) throws NKFException {
    INKFRequest innerRequest = request.getIssuableClone();
    aContext.createResponseFrom(innerRequest);
  }
  
  private void denyRequest(INKFRequestContext aContext) throws NKFException {
    String loginUrl;

    IHDSNode pdsState = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);
    if (pdsState.getFirstValue("//security_external") == null) {
      String url;
      if (!aContext.source("httpRequest:/url", String.class).endsWith("login")) {
        url= aContext.source("httpRequest:/url", String.class);
      } else {
        url= "/nk4um/";
      }

      aContext.sink("session:/message/class", "info");
      aContext.sink("session:/message/title", "Login Required");
      aContext.sink("session:/message/content", "You need to login to access this page.");

      aContext.sink("session:/loginRedirect", url);
    }

    aContext.sink("httpResponse:/redirect", aContext.source("nk4um:web:url:user:login", String.class));
  }
  
  private void maintenanceMode(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.sink("session:/message/class", "error");
    aContext.sink("session:/message/title", "Maintenance Mode");
    aContext.sink("session:/message/content", "nk4um is currently in maintenance mode.");
    
    INKFRequest req= util.createSourceRequest("active:java",
                                              null,
                                              new Arg("class", "uk.org.onegch.netkernel.nk4um.web.style.StyleAccessor"),
                                              new Arg("operand", "res:/uk/org/onegch/netkernel/nk4um/web/exception/maintenanceMode.xml"));
    
    aContext.createResponseFrom(req);
  }
}
