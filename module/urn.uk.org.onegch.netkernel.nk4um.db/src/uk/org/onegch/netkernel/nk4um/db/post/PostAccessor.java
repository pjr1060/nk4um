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

package uk.org.onegch.netkernel.nk4um.db.post;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class PostAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT   id,\n" +
                "         ( SELECT nk4um_forum_topic.forum_id\n" +
                "           FROM   nk4um_forum_topic\n" +
                "           WHERE  nk4um_forum_topic.id=nk4um_forum_topic_post.forum_topic_id)\n" +
                "         AS forum_id,\n" +
                "         forum_topic_id,\n" +
                "         author_id,\n" +
                "         posted_date,\n" +
                "         title,\n" +
                "         content,\n" +
                "         status\n" +
                "FROM     nk4um_forum_topic_post\n" +
                "WHERE    id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));

    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:topic", "nk4um:post");
  }
  
  @Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "INSERT INTO nk4um_forum_topic_post\n" +
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
                "    NOW(),\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    ?\n" +
                ");";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", sql),
                            new Arg("param", "arg:topicId"),
                            new Arg("param", "arg:authorId"),
                            new Arg("param", "arg:title"),
                            new Arg("param", "arg:content"),
                            new Arg("param", "arg:status"));
    
    util.cutGoldenThread("nk4um:post");
  }
}