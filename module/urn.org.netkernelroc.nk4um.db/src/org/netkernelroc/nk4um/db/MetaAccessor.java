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

package org.netkernelroc.nk4um.db;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseAccessorImpl;
import org.netkernelroc.mod.layer2.DatabaseUtil;

public class MetaAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT    ( SELECT     count(nk4um_forum.id)\n" +
                "            FROM       nk4um_forum)\n" +
                "            AS forum_count,\n" +
                "          ( SELECT     count(nk4um_visible_quick_forum_topic.id)\n" +
                "            FROM       nk4um_forum\n" +
                "            INNER JOIN nk4um_visible_quick_forum_topic ON nk4um_visible_quick_forum_topic.forum_id=nk4um_forum.id)\n" +
                "            AS topic_count,\n" +
                "          ( SELECT     count(nk4um_visible_forum_topic_post.id)\n" +
                "            FROM       nk4um_forum\n" +
                "            INNER JOIN nk4um_visible_quick_forum_topic ON nk4um_visible_quick_forum_topic.forum_id=nk4um_forum.id\n" +
                "            INNER JOIN nk4um_visible_forum_topic_post ON nk4um_visible_forum_topic_post.forum_topic_id=nk4um_visible_quick_forum_topic.id)\n" +
                "            AS post_count,\n" +
                "          ( SELECT     max(nk4um_visible_forum_topic_post.posted_date)\n" +
                "            FROM       nk4um_forum\n" +
                "            INNER JOIN nk4um_visible_quick_forum_topic ON nk4um_visible_quick_forum_topic.forum_id=nk4um_forum.id\n" +
                "            INNER JOIN nk4um_visible_forum_topic_post ON nk4um_visible_forum_topic_post.forum_topic_id=nk4um_visible_quick_forum_topic.id)\n" +
                "            AS last_post_time;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:forum", "nk4um:topic", "nk4um:post");
  }
}
