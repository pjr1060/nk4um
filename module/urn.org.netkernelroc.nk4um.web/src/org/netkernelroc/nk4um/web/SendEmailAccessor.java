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

package org.netkernelroc.nk4um.web;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import org.netkernelroc.mod.layer2.*;

public class SendEmailAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    try {
      IHDSNode header;
      IHDSNode pdsConfig= aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);

      if (pdsConfig.getFirstValue("//developer_email_mode") == null) {
        INKFRequest innerRequest = aContext.getThisRequest().getIssuableClone();
        //innerRequest.set
        aContext.createResponseFrom(innerRequest);
        header = aContext.source("arg:header", IHDSNode.class);
      } else {
        IHDSNode origHeader = aContext.source("arg:header", IHDSNode.class);
        HDSBuilder newHeader = new HDSBuilder();

        newHeader.pushNode("email");
        newHeader.addNode("to", pdsConfig.getFirstValue("//developer_email"));
        newHeader.addNode("subject", origHeader.getFirstValue("//subject") + " (to: " + origHeader.getFirstValue("//to") + ")");

        header = newHeader.getRoot();
      }

      INKFRequest sendmailReq= util.createSourceRequest("active:sendmail",
                                                        null,
                                                        new ArgByValue("header", header));
      for (int i = 0; i < aContext.getThisRequest().getArgumentCount(); i++) {
        String argName= aContext.getThisRequest().getArgumentName(i);
        if (!argName.equals("header")) {
          sendmailReq.addArgument(argName, "arg:" + argName);
        }
      }

      aContext.issueRequestForResponse(sendmailReq);
    } catch (Exception e) {
      aContext.logRaw(INKFRequestContext.LEVEL_SEVERE, ExceptionUtil.fullStackTrace(e));
    }
  }
}
