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

package org.netkernelroc.nk4um.web.forum.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import org.netkernel.layer0.representation.impl.HDSBuilder;
import org.netkernelroc.mod.layer2.*;

public class TopicListAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/org/netkernelroc/nk4um/web/forum/list/");

    boolean moderator= aContext.exists("nk4um:security:currentUser") &&
                       util.issueExistsRequest("nk4um:db:forum:moderator",
                                               new Arg("id", "arg:id"),
                                               new Arg("userId", "nk4um:security:currentUser"));

    HDSBuilder displayConfigParam= new HDSBuilder();
    displayConfigParam.pushNode("config");
    displayConfigParam.addNode("start", 0);
    displayConfigParam.addNode("length", 10);

    displayConfigParam.addNode("moderator", moderator);

    displayConfigParam.pushNode("sorting");
    displayConfigParam.pushNode("sort");
    displayConfigParam.addNode("column", getColumnName(3));
    displayConfigParam.addNode("direction", "DESC");
    displayConfigParam.popNode();
    displayConfigParam.popNode();

    IHDSNode topicList= util.issueSourceRequest("nk4um:db:topic:list",
                                                IHDSNode.class,
                                                new Arg("forumId", "arg:id"),
                                                new ArgByValue("config", displayConfigParam.getRoot()));

    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "topicList.xsl"),
                                      new Arg("operand", "topicList.xml"),
                                      new ArgByValue("topicList", topicList),
                                      new Arg("moderator", "arg:moderator"));
  }

  public static String getColumnName(Integer column) throws Exception {
    if (column == 0) {
      return "nk4um_forum_topic.title";
    } else if (column == 1) {
      return "(SELECT count (id) FROM nk4um_visible_quick_forum_topic_post WHERE nk4um_visible_quick_forum_topic_post.forum_topic_id=nk4um_forum_topic.id)";
    } else if (column == 2) {
      return "nk4um_forum_topic.view_count";
    } else if (column == 3) {
      return "(SELECT max (posted_date) FROM nk4um_visible_quick_forum_topic_post WHERE nk4um_visible_quick_forum_topic_post.forum_topic_id=nk4um_forum_topic.id)";
    } else if (column == 4) {
      return "nk4um_topic_status.display_order";
    } else {
      throw new Exception("Unexpected column number " + column);
    }
  }
}
