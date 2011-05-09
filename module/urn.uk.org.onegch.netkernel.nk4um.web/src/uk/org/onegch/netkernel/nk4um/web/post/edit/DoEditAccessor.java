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

package uk.org.onegch.netkernel.nk4um.web.post.edit;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import org.netkernelroc.mod.layer2.*;

public class DoEditAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/post/edit/");


    IHDSNode postDetails= util.issueSourceRequest("nk4um:db:post",
                                                  IHDSNode.class,
                                                  new Arg("id", "arg:id"));

    IHDSNode topicDetails= util.issueSourceRequest("nk4um:db:topic",
                                                   IHDSNode.class,
                                                   new ArgByValue("id", postDetails.getFirstValue("//forum_topic_id")));

    boolean moderator = aContext.exists("nk4um:security:currentUser") &&
                        util.issueExistsRequest("nk4um:db:forum:moderator",
                                                new ArgByValue("id", postDetails.getFirstValue("//forum_id")),
                                                new Arg("userId", "nk4um:security:currentUser"));

    if (!moderator && !postDetails.getFirstValue("//author_id").equals(aContext.source("nk4um:security:currentUser", Long.class))) {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Post Edit Failure");
      aContext.sink("session:/message/content", "You can not modify this post as you are not the author.");

      aContext.sink("httpResponse:/redirect", "../../index");
    } else if (!moderator && ((Boolean)topicDetails.getFirstValue("//locked"))) {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Post Edit Failure");
      aContext.sink("session:/message/content", "You can not modify your post as the topic is currently locked by a moderator.");

      aContext.sink("httpResponse:/redirect", "../../index");
    } else {
      String title= aContext.source("httpRequest:/param/title", String.class).trim();
      String content= aContext.source("httpRequest:/param/content", String.class).trim();

      boolean valid= true;
      HDSBuilder reasonsBuilder= new HDSBuilder();
      reasonsBuilder.pushNode("div");
      reasonsBuilder.addNode("p", "Edit post failed for the following reasons: ");
      reasonsBuilder.pushNode("ul");

      if (content.equals("")) {
        valid= false;
        reasonsBuilder.addNode("li", "Content must be supplied");
      }

      if (valid) {
        util.issueSinkRequest("nk4um:db:post",
                              null,
                              new Arg("id", "arg:id"),
                              new ArgByValue("title", title),
                              new ArgByValue("content", content));

        postDetails= util.issueSourceRequest("nk4um:db:post",
                                             IHDSNode.class,
                                             new Arg("id", "arg:id"));

        topicDetails= util.issueSourceRequest("nk4um:db:topic",
                                              IHDSNode.class,
                                              new ArgByValue("id", postDetails.getFirstValue("//forum_topic_id")));

        String emailTitle;
        if (postDetails.getFirstValue("//title") == null ||
                ((String)postDetails.getFirstValue("//title")).trim().equals("") ||
                postDetails.getFirstValue("//title").equals(topicDetails.getFirstValue("//title"))) {
          emailTitle = (String)topicDetails.getFirstValue("//title");
        } else {
          emailTitle =  topicDetails.getFirstValue("//title") + " / " + postDetails.getFirstValue("//title");
        }

        emailTitle = "nk4um Post Edited: " + emailTitle;

        IHDSNode userDetails= util.issueSourceRequest("nk4um:db:user",
                                                      IHDSNode.class,
                                                      new Arg("id", "nk4um:security:currentUser"));

        String url= aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url") +
                    "topic/" + postDetails.getFirstValue("//forum_topic_id") + "/index#" + aContext.source("arg:id", String.class);

        String displayName;
        if (userDetails.getFirstValue("//display_name") != null) {
          displayName = (String) userDetails.getFirstValue("//display_name");
        } else {
          displayName = (String) userDetails.getFirstValue("//email");
        }

        INKFRequest contentReq= util.createSourceRequest("active:freemarker",
                                                         null,
                                                         new Arg("operator", "editEmailTemplate.txt"),
                                                         new ArgByValue("editor", displayName),
                                                         new ArgByValue("title", emailTitle),
                                                         new ArgByValue("topic", topicDetails.getFirstValue("//title")),
                                                         new ArgByValue("url", url),
                                                         new ArgByValue("content", content));

        INKFRequest notificationReq= util.createSinkRequest("nk4um:web:notification:send",
                                                            null,
                                                            new ArgByValue("forumId", topicDetails.getFirstValue("//forum_id")),
                                                            new Arg("authorId", "nk4um:security:currentUser"),
                                                            new ArgByValue("title", emailTitle),
                                                            new ArgByRequest("content", contentReq),
                                                            new ArgByValue("topicId", postDetails.getFirstValue("//forum_topic_id")));
        aContext.issueAsyncRequest(notificationReq);

        // has the post been modified by a moderator who wasn't the author
        if ((Long)postDetails.getFirstValue("//author_id") != (long)aContext.source("nk4um:security:currentUser", Long.class)) {
          IHDSNode authorDetails= util.issueSourceRequest("nk4um:db:user",
                                                          IHDSNode.class,
                                                          new ArgByValue("id", postDetails.getFirstValue("//author_id")));

          contentReq= util.createSourceRequest("active:freemarker",
                                               null,
                                               new Arg("operator", "moderatorEditEmailTemplate.txt"),
                                               new ArgByValue("editor", displayName),
                                               new ArgByValue("title", emailTitle),
                                               new ArgByValue("topic", topicDetails.getFirstValue("//title")),
                                               new ArgByValue("url", url),
                                               new ArgByValue("content", content));

          HDSBuilder headerBuilder= new HDSBuilder();
          headerBuilder.pushNode("email");
          headerBuilder.addNode("to", authorDetails.getFirstValue("//email"));
          headerBuilder.addNode("subject", "nk4um: A moderator has edited your post");

          INKFRequest notifyAuthor = util.createSourceRequest("active:sendmail",
                                                              null,
                                                              new ArgByValue("header", headerBuilder.getRoot()),
                                                              new ArgByRequest("body", contentReq));
          aContext.issueRequest(notifyAuthor);
        }

        aContext.sink("session:/message/class", "success");
        aContext.sink("session:/message/title", "Edit post successful");
        aContext.sink("session:/message/content", "Edit post successful :-)");

        aContext.sink("httpResponse:/redirect", "../../index#" + aContext.source("arg:id", String.class));
      } else {
        aContext.sink("session:/message/class", "error");
        aContext.sink("session:/message/title", "Edit post failure");
        aContext.sink("session:/message/content", reasonsBuilder.getRoot());

        if (aContext.exists("httpRequest:/params")) {
          aContext.sink("session:/formData/name", "edit");
          aContext.sink("session:/formData/params", aContext.source("httpRequest:/params"));
        }

        aContext.sink("httpResponse:/redirect", "edit");
      }
    }
  }
}
