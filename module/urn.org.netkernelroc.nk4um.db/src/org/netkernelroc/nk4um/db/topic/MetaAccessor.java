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
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import org.netkernel.layer0.representation.IHDSNodeList;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import org.netkernelroc.mod.layer2.Arg;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseAccessorImpl;
import org.netkernelroc.mod.layer2.DatabaseUtil;
import org.netkernelroc.nk4um.db.SimpleHDSPredicate;

public class MetaAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    IHDSNodeList topicVisiblePosts = util.issueSourceRequest("nk4um:db:post:list:allVisible",
                                                             IHDSNodeList.class,
                                                             new Arg("topicId", "arg:id"));
    long postCount = topicVisiblePosts.size();

    HDSBuilder metaBuilder = new HDSBuilder();
    metaBuilder.pushNode("root");
    metaBuilder.pushNode("row");
    metaBuilder.addNode("post_count", postCount);
    metaBuilder.addNode("last_post_time", topicVisiblePosts.getFirstValue("//row/posted_date"));
    
    aContext.createResponseFrom(metaBuilder.getRoot());
  }
}
