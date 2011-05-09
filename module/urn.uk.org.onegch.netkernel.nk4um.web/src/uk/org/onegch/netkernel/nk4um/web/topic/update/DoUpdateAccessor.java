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

package uk.org.onegch.netkernel.nk4um.web.topic.update;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import org.netkernel.layer0.representation.impl.HDSBuilder;
import org.netkernelroc.mod.layer2.*;

public class DoUpdateAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    String url;
    if (aContext.exists("httpRequest:/header/Referer") &&
        aContext.source("httpRequest:/header/Referer", String.class).contains("/nk4um/") &&
        !aContext.source("httpRequest:/header/Referer", String.class).contains("doUpdate")) {
      url= aContext.source("httpRequest:/header/Referer", String.class);
    } else {
      url= "/nk4um/";
    }
    
    IHDSNode topic= util.issueSourceRequest("nk4um:db:topic",
                                            IHDSNode.class,
                                            new Arg("id", "arg:id"));
    
    if(aContext.exists("nk4um:security:currentUser") &&
                       util.issueExistsRequest("nk4um:db:forum:moderator",
                                               new ArgByValue("id", topic.getFirstValue("//forum_id")),
                                               new Arg("userId", "nk4um:security:currentUser"))) {
      IHDSNode status= aContext.source("httpRequest:/params", IHDSNode.class);
      HDSBuilder statusBuilder= new HDSBuilder();
      statusBuilder.pushNode("root");
      statusBuilder.addNode("status", status.getFirstValue("//status"));
      statusBuilder.addNode("locked", status.getFirstValue("//locked") != null);

      util.issueSinkRequest("nk4um:db:topic:status",
                            new PrimaryArgByValue(statusBuilder.getRoot()),
                            new Arg("id", "arg:id"));

      aContext.sink("session:/message/class", "success");
      aContext.sink("session:/message/title", "Topic status updated");
      aContext.sink("session:/message/content", "The topic status has been updated.");
    } else {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Topic status not changed");
      aContext.sink("session:/message/content", "You are not a moderator for this forum, and so cannot change the status of the topic.");
    }
    aContext.sink("httpResponse:/redirect", url);
    
  }
}
