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
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseAccessorImpl;
import org.netkernelroc.mod.layer2.DatabaseUtil;

public class ModeratorFullListAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String existsSql= "SELECT     nk4um_forum_moderator.user_id\n" +
                      "FROM       nk4um_forum\n" +
                      "INNER JOIN nk4um_forum_moderator ON nk4um_forum_moderator.forum_id=nk4um_forum.id\n" +
                      "WHERE      nk4um_forum.id=?\n" +
                      "UNION\n" +
                      "SELECT     nk4um_forum_group_moderator.user_id\n" +
                      "FROM       nk4um_forum\n" +
                      "INNER JOIN nk4um_forum_group_moderator ON nk4um_forum_group_moderator.forum_group_id=nk4um_forum.forum_group_id\n" +
                      "WHERE      nk4um_forum.id=?\n" +
                      "UNION\n" +
                      "SELECT     nk4um_user.id\n" +
                      "FROM       nk4um_user\n" +
                      "WHERE      nk4um_user.role_name='Administrator';";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         new ArgByValue("operand", existsSql),
                                                         new ArgByValue("param", aContext.source("arg:id")),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    
    util.attachGoldenThread("nk4um:forum", "nk4um:forumGroup");
  }
}
