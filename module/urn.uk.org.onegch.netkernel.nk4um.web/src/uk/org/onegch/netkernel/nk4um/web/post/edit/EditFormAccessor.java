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

import net.sf.saxon.s9api.XdmNode;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class EditFormAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
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
      aContext.sink("session:/message/title", "Topic Edit Failure");
      aContext.sink("session:/message/content", "You can not modify this post as you are not the author.");

      aContext.sink("httpResponse:/redirect", "../../index");
    } else if (!moderator && ((Boolean)topicDetails.getFirstValue("//locked"))) {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Topic Edit Failure");
      aContext.sink("session:/message/content", "You can not modify your post as the topic is currently locked by a moderator.");

      aContext.sink("httpResponse:/redirect", "../../index");
    } else {
      IHDSNode params;

      if (aContext.exists("session:/formData/name") &&
          aContext.source("session:/formData/name", String.class).equals("editPost")) {
        params= aContext.source("session:/formData/params", IHDSNode.class);
        aContext.delete("session:/formData/name");
        aContext.delete("session:/formData/params");
      } else {
        HDSBuilder builder= new HDSBuilder();
        builder.pushNode("root");
        builder.addNode("title", postDetails.getFirstValue("//title"));
        builder.addNode("content", postDetails.getFirstValue("//content"));
        params= builder.getRoot();
      }

      XdmNode formNode= util.issueSourceRequest("active:xslt2",
                                                XdmNode.class,
                                                new Arg("operator", "../../common/form-template.xsl"),
                                                new Arg("operand", "edit.xml"),
                                                new ArgByValue("params", params));

      util.issueSourceRequestAsResponse("active:xrl2",
                                        new ArgByValue("template", formNode),
                                        new Arg("id", "arg:id"));
    }
  }
}
