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

package uk.org.onegch.netkernel.nk4um.web.forum.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class TopicAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/forum/list/");
    
    IHDSNode topic= util.issueSourceRequest("nk4um:db:topic",
                                             IHDSNode.class,
                                             new Arg("id", "arg:id"));
    
    INKFRequest topicMetaReq= util.createSourceRequest("nk4um:db:topic:meta",
                                                       IHDSNode.class,
                                                       new Arg("id", "arg:id"));
    
    boolean moderator= aContext.exists("nk4um:security:currentUser") &&
                       util.issueExistsRequest("nk4um:db:forum:moderator",
                                               new ArgByValue("id", topic.getFirstValue("//forum_id")),
                                               new Arg("userId", "nk4um:security:currentUser"));
    
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "topic.xsl"),
                                      new Arg("operand", "topic.xml"),
                                      new ArgByValue("topic", topic),
                                      new ArgByRequest("topicMeta", topicMetaReq),
                                      new ArgByValue("moderator", moderator));
  }
}
