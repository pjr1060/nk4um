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

package uk.org.onegch.netkernel.nk4um.web.post.update;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import uk.org.onegch.netkernel.layer2.*;

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
    
    IHDSNode post= util.issueSourceRequest("nk4um:db:post",
                                           IHDSNode.class,
                                           new Arg("id", "arg:id"));

    IHDSNode topic= util.issueSourceRequest("nk4um:db:topic",
                                            IHDSNode.class,
                                            new ArgByValue("id", post.getFirstValue("//forum_topic_id")));

    boolean admin= aContext.exists("arg:admin") && aContext.source("arg:admin", Boolean.class);

    if(admin || aContext.exists("nk4um:security:currentUser") &&
                       util.issueExistsRequest("nk4um:db:forum:moderator",
                                               new ArgByValue("id", post.getFirstValue("//forum_id")),
                                               new Arg("userId", "nk4um:security:currentUser"))) {
      util.issueSinkRequest("nk4um:db:post:status",
                            new PrimaryArg("httpRequest:/param/status"),
                            new Arg("id", "arg:id"));
      
      if (post.getFirstValue("//status").equals("moderation") &&
          aContext.source("httpRequest:/param/status", String.class).equals("active")) {
        // is this the only post in the topic
        IHDSNode topicPostList = util.issueSourceRequest("nk4um:db:post:list",
                                                         IHDSNode.class,
                                                         new ArgByValue("topicId", post.getFirstValue("//forum_topic_id")));

        IHDSNode userDetails = util.issueSourceRequest("nk4um:db:user",
                                                       IHDSNode.class,
                                                       new ArgByValue("id", post.getFirstValue("//author_id")));

        String displayName;
        if (userDetails.getFirstValue("//display_name") != null) {
          displayName = (String) userDetails.getFirstValue("//display_name");
        } else {
          displayName = (String) userDetails.getFirstValue("//email");
        }

        INKFRequest contentReq;
        String emailTitle;
        String viewUrl = (String) aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url");

        if (topicPostList.getNodes("//row").size() == 1) {
          viewUrl = "topic/" + topic.getFirstValue("//id") + "/index";

          contentReq= util.createSourceRequest("active:freemarker",
                                              null,
                                              new Arg("operator", "res:/uk/org/onegch/netkernel/nk4um/web/topic/add/addEmailTemplate.txt"),
                                              new ArgByValue("poster", displayName),
                                              new ArgByValue("forum", topic.getFirstValue("//forum")),
                                              new ArgByValue("topic", topic.getFirstValue("//title")),
                                              new ArgByValue("url", viewUrl),
                                              new ArgByValue("content", post.getFirstValue("//content")));

          emailTitle = "nk4um New Topic: " + topic.getFirstValue("//title");
        } else {
          viewUrl = "topic/" + topic.getFirstValue("//id") + "/index";

          contentReq= util.createSourceRequest("active:freemarker",
                                               null,
                                               new Arg("operator", "res:/uk/org/onegch/netkernel/nk4um/web/post/add/addEmailTemplate.txt"),
                                               new ArgByValue("poster", displayName),
                                               new ArgByValue("title", post.getFirstValue("//title")),
                                               new ArgByValue("topic", topic.getFirstValue("//title")),
                                               new ArgByValue("url", viewUrl),
                                               new ArgByValue("content", post.getFirstValue("//content")));

          emailTitle = "nk4um Reply Posted: " + topic.getFirstValue("//title");
        }

        INKFRequest notificationReq= util.createSinkRequest("nk4um:web:notification:send",
                                                            null,
                                                            new ArgByValue("forumId", post.getFirstValue("//forum_id")),
                                                            new ArgByValue("authorId", post.getFirstValue("//author_id")),
                                                            new ArgByValue("title", emailTitle),
                                                            new ArgByRequest("content", contentReq),
                                                            new ArgByValue("topicId", topic.getFirstValue("//id")));

        aContext.issueAsyncRequest(notificationReq);

        HDSBuilder headerBuilder= new HDSBuilder();
        headerBuilder.pushNode("email");
        headerBuilder.addNode("to", userDetails.getFirstValue("//email"));
        headerBuilder.addNode("subject", "nk4um Message Approved");

        String emailBody= "Dear " + displayName + ",\n\n" +
                        "Your nk4um post has been approved by a moderator. To see your post:\n" +
                        viewUrl;
        
        INKFRequest approvedEmailReq = util.createSourceRequest("active:sendmail",
                                                                null,
                                                                new ArgByValue("header", headerBuilder.getRoot()),
                                                                new ArgByValue("body", emailBody));

        aContext.issueAsyncRequest(approvedEmailReq);
      }

      if (!admin) {
        aContext.sink("session:/message/class", "success");
        aContext.sink("session:/message/title", "Post status updated");
        aContext.sink("session:/message/content", "The post status has been updated.");
      }
    } else {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Post status not changed");
      aContext.sink("session:/message/content", "You are not a moderator for this forum, and so cannot change the status of the topic.");
    }
    aContext.sink("httpResponse:/redirect", url);
    
  }
}
