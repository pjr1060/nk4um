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

package uk.org.onegch.netkernel.nk4um.db.forum;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ModeratorAccessor extends DatabaseAccessorImpl {
@Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String insertModeratorSql= "INSERT INTO nk4um_forum_moderator\n" +
                               "(\n" +
                               "    forum_id,\n" +
                               "    user_id\n" +
                               ") VALUES (\n" +
                               "    ?,\n" +
                               "    ?\n" +
                                ");";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", insertModeratorSql),
                            new ArgByValue("param", aContext.source("arg:id")),
                            new ArgByValue("param", aContext.source("arg:userId")));
    
    util.cutGoldenThread("nk4um:forum");
  }
  
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String existsSql= "SELECT     nk4um_forum_moderator.user_id\n" +
                      "FROM       nk4um_forum\n" +
                      "INNER JOIN nk4um_forum_moderator ON nk4um_forum_moderator.forum_id=nk4um_forum.id\n" +
                      "WHERE      nk4um_forum.id=?\n" +
                      "AND        nk4um_forum_moderator.user_id=?\n" +
                      "UNION\n" +
                      "SELECT     nk4um_forum_group_moderator.user_id\n" +
                      "FROM       nk4um_forum\n" +
                      "INNER JOIN nk4um_forum_group_moderator ON nk4um_forum_group_moderator.forum_group_id=nk4um_forum.forum_group_id\n" +
                      "WHERE      nk4um_forum.id=?\n" +
                      "AND        nk4um_forum_group_moderator.user_id=?" +
                      "UNION\n" +
                      "SELECT     nk4um_user.id\n" +
                      "FROM       nk4um_user\n" +
                      "WHERE      nk4um_user.id=?\n" +
                      "AND        nk4um_user.role_name='Administrator';";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                         new ArgByValue("operand", existsSql),
                                                         new ArgByValue("param", aContext.source("arg:id")),
                                                         new ArgByValue("param", aContext.source("arg:userId")),
                                                         new ArgByValue("param", aContext.source("arg:id")),
                                                         new ArgByValue("param", aContext.source("arg:userId")),
                                                         new ArgByValue("param", aContext.source("arg:userId")));
    
    resp.setHeader("no-cache", null);
    
    util.attachGoldenThread("nk4um:forum", "nk4um:forumGroup");
  }
}
