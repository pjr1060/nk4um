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

package uk.org.onegch.netkernel.nk4um.web.topic.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import org.netkernelroc.mod.layer2.*;

public class PostListAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/topic/list/");

    INKFRequest postListReq;
    boolean moderator;

    if (aContext.exists("arg:id")) {
      postListReq = util.createSourceRequest("nk4um:db:post:list",
                                             IHDSNode.class,
                                             new Arg("topicId", "arg:id"));

      IHDSNode topic = util.issueSourceRequest("nk4um:db:topic",
                                               IHDSNode.class,
                                               new Arg("id", "arg:id"));

      moderator = aContext.source("arg:displayModeration", Boolean.class) &&
              aContext.exists("nk4um:security:currentUser") &&
              util.issueExistsRequest("nk4um:db:forum:moderator",
                                      new ArgByValue("id", topic.getFirstValue("//forum_id")),
                                      new Arg("userId", "nk4um:security:currentUser"));
    } else {
      postListReq = util.createSourceRequest("nk4um:db:post:moderationList",
                                             IHDSNode.class);
      moderator = true;
    }

    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "postList.xsl"),
                                      new Arg("operand", "postList.xml"),
                                      new Arg("displayModeration", "arg:displayModeration"),
                                      new ArgByRequest("postList", postListReq),
                                      new ArgByValue("moderator", moderator));
  }
}
