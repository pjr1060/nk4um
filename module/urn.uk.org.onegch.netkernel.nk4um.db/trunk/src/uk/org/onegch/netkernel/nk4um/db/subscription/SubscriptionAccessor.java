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

package uk.org.onegch.netkernel.nk4um.db.subscription;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class SubscriptionAccessor extends DatabaseAccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT id\n" +
                "FROM   nk4um_forum_subscription\n" +
                "WHERE  user_id=?\n" +
                "AND    forum_id=?\n" +
                "AND    notification_type_name='email'";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                         Boolean.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:userId")),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:forum", "nk4um:user", "nk4um:subscription");
  }
  
  @Override
  public void onDelete(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "DELETE\n" +
                "FROM   nk4um_forum_subscription\n" +
                "WHERE  user_id=?\n" +
                "AND    forum_id=?\n" +
                "AND    notification_type_name='email';";

    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", sql),
                            new ArgByValue("param", aContext.source("arg:userId")),
                            new ArgByValue("param", aContext.source("arg:id")));
    
    util.cutGoldenThread("nk4um:subscription");
  }
  
  @Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "INSERT INTO nk4um_forum_subscription\n" +
                "(\n" +
                "    user_id,\n" +
                "    forum_id,\n" +
                "    notification_type_name\n" +
                ") VALUES (\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    'email'\n" +
                ");";
    
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", sql),
                            new ArgByValue("param", aContext.source("arg:userId")),
                            new ArgByValue("param", aContext.source("arg:id")));
    
    util.cutGoldenThread("nk4um:subscription");
  }
}
