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

package uk.org.onegch.netkernel.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class PasswordAccessor extends DatabaseAccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String encryptedPassword= util.issueSourceRequest("active:sha512",
                                                      String.class,
                                                      new ArgByValue("operand", aContext.source("arg:password")));
    
    String sql= "SELECT id\n" +
                "FROM   nk4um_user_account\n" +
                "WHERE  id=?\n" +
                "AND    password=?";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                         Boolean.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")),
                                                         new ArgByValue("param", encryptedPassword));
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
  
  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String encryptedPassword= util.issueSourceRequest("active:sha512",
                                                      String.class,
                                                      new ArgByValue("operand", aContext.source("arg:password")));
    
    String sql= "UPDATE nk4um_user_account\n" +
                "SET    password=?\n" +
                "WHERE  id=?;";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSUpdate",
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", encryptedPassword),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    resp.setHeader("no-cache", null);
    util.cutGoldenThread("nk4um:user");
  }
}
