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

package org.netkernelroc.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseAccessorImpl;
import org.netkernelroc.mod.layer2.DatabaseUtil;

public class MetaAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT    ( SELECT     count(nk4um_forum_topic_post.id)\n" +
                "            FROM       nk4um_forum_topic_post\n" +
                "            WHERE      nk4um_forum_topic_post.author_id=nk4um_user.id)\n" +
                "            AS post_count\n" +
                "FROM      nk4um_user\n" +
                "WHERE     nk4um_user.id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user", "nk4um:post");
  }

  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    IHDSNode details= aContext.sourcePrimary(IHDSNode.class);

    String existsSql = "SELECT user_account_id FROM nk4um_user_meta WHERE user_account_id=?";

    String updateMetaSql;
    if (util.issueSourceRequest("active:sqlPSBooleanQuery",
                                Boolean.class,
                                new ArgByValue("operand", existsSql),
                                new ArgByValue("param", aContext.source("arg:id")))) {
      updateMetaSql= "UPDATE nk4um_user_meta\n" +
                     "SET    display_name=?\n" +
                     "WHERE  user_account_id=?";
    } else {
      updateMetaSql= "INSERT INTO nk4um_user_meta (display_name, user_account_id, role_name)\n" +
                     "VALUES                      (?, ?, 'User')";
    }
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", updateMetaSql),
                            new ArgByValue("param", details.getFirstValue("//display_name")),
                            new ArgByValue("param", aContext.source("arg:id")));

    util.cutGoldenThread("nk4um:user");
  }
}
