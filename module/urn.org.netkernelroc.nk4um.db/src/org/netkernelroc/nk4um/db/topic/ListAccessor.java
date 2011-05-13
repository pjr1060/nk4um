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

import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseAccessorImpl;
import org.netkernelroc.mod.layer2.DatabaseUtil;

public class ListAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    IHDSNode config = util.escapeHDS(aContext.source("arg:config", IHDSNode.class));

    String start = "";
    if (config.getFirstValue("/config/start") != null) {
      start = "OFFSET '" + config.getFirstValue("/config/start") + "'\n";
    }

    String limit = "";
    if (config.getFirstValue("/config/length") != null) {
      limit = "LIMIT '" + config.getFirstValue("/config/length") + "'\n";
    }

    String sort = "";
    if (config.getFirstNode("/config/sorting/sort") != null) {
      sort = "";
      String nextSep = ",\n";
      for (IHDSNode sortNode : config.getNodes("/config/sorting/sort")) {
        sort += nextSep + sortNode.getFirstValue("column") + " " + getDirection((String) sortNode.getFirstValue("direction"));
        nextSep = ",\n";
      }
      sort += "\n";
    }

    String search = "";
    if (config.getFirstNode("/config/search") != null) {
      search += "AND ( nk4um_forum_topic.title like '%" + config.getFirstValue("/config/search") + "%')\n";
    }


    String moderator = "";
    if ((config.getFirstNode("/config/moderator") == null || !(Boolean)config.getFirstValue("/config/moderator"))) {
      moderator = "AND       (     nk4um_topic_status.visible\n" +
                  "            AND (SELECT count(id)\n" +
                  "                 FROM   nk4um_forum_topic_post\n" +
                  "                 WHERE  (SELECT  visible\n" +
                  "                         FROM    nk4um_post_status\n" +
                  "                         WHERE   nk4um_post_status.id=nk4um_forum_topic_post.status)\n" +
                  "                 AND     nk4um_forum_topic_post.forum_topic_id=nk4um_forum_topic.id)>0)\n";
    }


    String sql= "SELECT     nk4um_forum_topic.id,\n" +
                "           TRUE AS visible\n" +
                "FROM       nk4um_forum_topic\n" +
                "INNER JOIN nk4um_topic_status ON nk4um_topic_status.id=nk4um_forum_topic.status\n" +
                "WHERE      forum_id=?\n" +
                moderator +
                search +
                "ORDER BY   nk4um_topic_status.display_order\n" +
                sort +
                limit +
                start +
                ";";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:forumId")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:topic");
  }

  private String getDirection(String direction) {
    if (direction != null && direction.trim().equalsIgnoreCase("DESC")) {
      return "DESC";
    } else {
      return "";
    }
  }
}
