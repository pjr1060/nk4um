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

package org.netkernelroc.nk4um.web.security;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.NKFException;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.urii.SimpleIdentifierImpl;
import org.netkernel.request.IRequestScopeLevel;
import org.netkernel.request.impl.RequestScopeLevelImpl;
import org.netkernel.urii.IIdentifier;
import org.netkernel.urii.ISpace;
import org.netkernel.urii.impl.Version;
import org.netkernelroc.mod.layer2.AccessorUtil;
import org.netkernelroc.mod.layer2.Layer2AccessorImpl;

public class CurrentUserAccessor extends Layer2AccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    boolean loggedIn;
    IHDSNode pdsState;

    try {
      pdsState = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);

      if (pdsState.getFirstValue("//security_external") == null) {
        loggedIn = aContext.exists("session:/currentUser");
      } else {
        INKFRequest request = util.createExistsRequest((String) pdsState.getFirstValue("//security_userIdResource"));
        request.setRequestScope(buildRequestScope(aContext, (String) pdsState.getFirstValue("//security_gatekeeperModule")));
        
        loggedIn = (Boolean)aContext.issueRequest(request);
      }
      aContext.createResponseFrom(loggedIn);
    } catch (Exception e) {
      aContext.createResponseFrom(false);
    }
  }

  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    Object result;
    IHDSNode pdsState;

    pdsState = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);

    if (pdsState.getFirstValue("//security_external") == null) {
      result = aContext.source("session:/currentUser");
    } else {
      INKFRequest request = util.createSourceRequest((String) pdsState.getFirstValue("//security_userIdResource"), null);
      request.setRequestScope(buildRequestScope(aContext, (String) pdsState.getFirstValue("//security_gatekeeperModule")));

      result = aContext.issueRequest(request);
    }
    aContext.createResponseFrom(result);
  }

  @Override
  public void onSink(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    IHDSNode pdsState;

    pdsState = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);

    if (pdsState.getFirstValue("//security_external") == null) {
      aContext.sink("session:/currentUser", aContext.sourcePrimary(Object.class));
    } else {
      throw new Exception("Internal error: cannot sync to external user resource.");
    }
  }

  private IRequestScopeLevel buildRequestScope(INKFRequestContext aContext, String moduleUri) throws NKFException {
    IIdentifier uri = new SimpleIdentifierImpl(moduleUri);
    Version version = null;
    ISpace space = aContext.getKernelContext().getKernel().getSpace(uri, version, version);
    return RequestScopeLevelImpl.appendScopeLevel(RequestScopeLevelImpl.createOrphanedRootScopeLevel(space, aContext.getKernelContext().getRequestScope()), aContext.getKernelContext().getRequestScope());
  }
}
