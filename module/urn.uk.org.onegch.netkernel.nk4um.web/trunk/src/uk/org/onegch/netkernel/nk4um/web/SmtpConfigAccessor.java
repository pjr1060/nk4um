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

package uk.org.onegch.netkernel.nk4um.web;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class SmtpConfigAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    IHDSNode pdsState;
    
    try {
      pdsState= aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);
    } catch (Exception e) {
      throw new Exception("nk4um not initialized", e);
    }
      
    HDSBuilder b = new HDSBuilder();
    
    
    if (pdsState.getFirstValue("//smtpGateway") != null &&
        pdsState.getFirstValue("//smtpSender") != null) {
      b.pushNode("SMTPConfig");
      b.addNode("gateway", pdsState.getFirstValue("//smtpGateway"));
      if (pdsState.getFirstValue("//smtpPort") != null &&
          !pdsState.getFirstValue("//smtpPort").equals("")) {
        b.addNode("port", pdsState.getFirstValue("//smtpPort"));
      }
      if (pdsState.getFirstValue("//smtpUser") != null &&
          !pdsState.getFirstValue("//smtpUser").equals("")) {
        b.addNode("user", pdsState.getFirstValue("//smtpUser"));
      }
      if (pdsState.getFirstValue("//smtpPassword") != null &&
          !pdsState.getFirstValue("//smtpPassword").equals("")) {
        b.addNode("password", pdsState.getFirstValue("//smtpPassword"));
      }
      
      if (pdsState.getFirstValue("//smtpSecurity").equals("tls")) {
        b.addNode("tls", "true");
      } else if (pdsState.getFirstValue("//smtpSecurity").equals("ssl")) {
        b.addNode("ssl", "true");
      }
      
      b.addNode("sender", pdsState.getFirstValue("//smtpSender"));
    } else {
      throw new Exception("nk4um not initialized");
    }
    
    aContext.createResponseFrom(b.getRoot());
  }
}
