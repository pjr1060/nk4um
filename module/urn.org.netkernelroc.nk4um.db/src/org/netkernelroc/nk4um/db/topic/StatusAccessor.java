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

package org.netkernelroc.nk4um.db.topic;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import org.netkernelroc.mod.layer2.Arg;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseAccessorImpl;
import org.netkernelroc.mod.layer2.DatabaseUtil;

public class StatusAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "UPDATE nk4um_forum_topic\n" +
                "SET    status=(SELECT id\n" +
                "               FROM   nk4um_topic_status\n" +
                "               WHERE nk4um_topic_status.status=?),\n" +
                "       locked=?\n" +
                "WHERE  id=?\n";

    IHDSNode statusParams= aContext.sourcePrimary(IHDSNode.class);

    util.issueSourceRequest("active:sqlPSUpdate",
                            IHDSNode.class,
                            new ArgByValue("operand", sql),
                            new ArgByValue("param", statusParams.getFirstValue("//status")),
                            new ArgByValue("param", statusParams.getFirstValue("//locked")),
                            new ArgByValue("param", aContext.source("arg:id")));

    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new Arg("operand", "res:/org/netkernelroc/nk4um/db/topic/updateVisibleTopic.sql"),
                            new ArgByValue("param", aContext.source("arg:id")));
    
    util.cutGoldenThread("nk4um:topic");
  }
}
