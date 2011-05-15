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

import org.netkernelroc.mod.layer2.Arg;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseAccessorImpl;
import org.netkernelroc.mod.layer2.DatabaseUtil;

public class TopicAccessor extends DatabaseAccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT   id\n" +
                "FROM     nk4um_forum_topic\n" +
                "WHERE    id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:topic");
  }
  
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT     nk4um_forum_topic.id,\n" +
                "           nk4um_forum_group.title AS forum_group,\n" +
                "           (SELECT status\n" +
                "            FROM nk4um_topic_status\n" +
                "            WHERE nk4um_topic_status.id=nk4um_forum_topic.status) AS status,\n" +
                "          (     nk4um_topic_status.visible\n" +
                "            AND (SELECT count(id)\n" +
                "                 FROM   nk4um_forum_topic_post\n" +
                "                 WHERE  (SELECT  visible\n" +
                "                         FROM    nk4um_post_status\n" +
                "                         WHERE   nk4um_post_status.id=nk4um_forum_topic_post.status\n)" +
                "                 AND     nk4um_forum_topic_post.forum_topic_id=nk4um_forum_topic.id)>0) AS visible,\n" +
                "           nk4um_topic_status.display_order AS status_order,\n" +
                "           nk4um_forum.title AS forum,\n" +
                "           nk4um_forum_topic.forum_id,\n" +
                "           nk4um_forum_topic.author_id,\n" +
                "           nk4um_forum_topic.posted_date,\n" +
                "           nk4um_forum_topic.title,\n" +
                "           nk4um_forum_topic.locked\n" +
                "FROM       nk4um_forum_topic\n" +
                "INNER JOIN nk4um_forum        ON nk4um_forum.id=nk4um_forum_topic.forum_id\n" +
                "INNER JOIN nk4um_forum_group  ON nk4um_forum_group.id=nk4um_forum.forum_group_id\n" +
                "INNER JOIN nk4um_topic_status ON nk4um_topic_status.id=nk4um_forum_topic.status\n" +
                "WHERE      nk4um_forum_topic.id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:topic");
  }
  
  @Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String nextIdSql= "SELECT nextval('nk4um_forum_topic_id_seq') AS id;";
    IHDSNode nextIdNode= util.issueSourceRequest("active:sqlPSQuery",
                                                 IHDSNode.class,
                                                 new ArgByValue("operand", nextIdSql));
    Long nextId= (Long)nextIdNode.getFirstValue("//id");
    
    String insertTopicSql= "INSERT INTO nk4um_forum_topic\n" +
                           "(\n" +
                           "    id,\n" +
                           "    forum_id\n," +
                           "    author_id\n," +
                           "    posted_date\n," +
                           "    title,\n" +
                           "    status\n" +
                           ") VALUES (\n" +
                           "    ?,\n" +
                           "    ?,\n" +
                           "    ?,\n" +
                           "    NOW(),\n" +
                           "    ?,\n" +
                           "    (SELECT id\n" +
                           "     FROM nk4um_topic_status\n" +
                           "     WHERE nk4um_topic_status.status='active')\n" +
                           ");";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", insertTopicSql),
                            new ArgByValue("param", nextId),
                            new ArgByValue("param", aContext.source("arg:forumId")),
                            new ArgByValue("param", aContext.source("arg:authorId")),
                            new ArgByValue("param", aContext.source("arg:title")));
    
    String insertPostSql= "INSERT INTO nk4um_forum_topic_post\n" +
                          "(\n" +
                          "    forum_topic_id,\n" +
                          "    author_id,\n" +
                          "    posted_date,\n" +
                          "    title,\n" +
                          "    content,\n" +
                          "    status\n" +
                          ") VALUES (\n" +
                          "    ?,\n" +
                          "    ?,\n" +
                          "    (SELECT posted_date\n" +
                          "     FROM   nk4um_forum_topic\n" +
                          "     WHERE  id=?),\n" +
                          "    ?,\n" +
                          "    ?,\n" +
                          "    (SELECT id\n" +
                          "     FROM nk4um_post_status\n" +
                          "     WHERE nk4um_post_status.status=?)\n" +
                          ");";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", insertPostSql),
                            new ArgByValue("param", nextId),
                            new ArgByValue("param", aContext.source("arg:authorId")),
                            new ArgByValue("param", nextId),
                            new ArgByValue("param", aContext.source("arg:title")),
                            new ArgByValue("param", aContext.source("arg:content")),
                            new ArgByValue("param", aContext.source("arg:postStatus")));

    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new Arg("operand", "res:/org/netkernelroc/nk4um/db/topic/updateVisibleTopic.sql"),
                            new ArgByValue("param", nextId));

    util.cutGoldenThread("nk4um:post", "nk4um:topic");
    
    aContext.createResponseFrom(nextId);
  }
}
