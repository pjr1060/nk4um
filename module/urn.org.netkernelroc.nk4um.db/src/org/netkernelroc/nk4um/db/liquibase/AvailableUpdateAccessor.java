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

package org.netkernelroc.nk4um.db.liquibase;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseAccessorImpl;
import org.netkernelroc.mod.layer2.DatabaseUtil;

public class AvailableUpdateAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    HDSBuilder params= new HDSBuilder();
    params.pushNode("parameters");

    IHDSNode pdsState;

    try {
      pdsState= aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);
    } catch (Exception e) {
      throw new Exception("nk4um not initialized", e);
    }

    String context;
    if (pdsState.getFirstValue("//security_external") == null) {
      context= "builtin-user";

      params.pushNode("parameter");
      params.addNode("name", "user.table");
      params.addNode("value", "nk4um_user_account");
      params.popNode();
      params.pushNode("parameter");
      params.addNode("name", "user.id");
      params.addNode("value", "id");
      params.popNode();
    } else {
      context= "external-user";

      params.pushNode("parameter");
      params.addNode("name", "user.table");
      params.addNode("value", pdsState.getFirstValue("//security_userTable"));
      params.popNode();
      params.pushNode("parameter");
      params.addNode("name", "user.id");
      params.addNode("value", pdsState.getFirstValue("//security_userTableId"));
      params.popNode();
    }
    
    util.issueSourceRequestAsResponse("active:liquibase-update-available",
                                      Boolean.class,
                                      new ArgByValue("context", "main," + context),
                                      new ArgByValue("changelog", "res:/org/netkernelroc/nk4um/db/liquibase/master.xml"),
                                      new ArgByValue("parameters", params.getRoot()));
    
    util.attachGoldenThread("nk4um:all");
  }
}
