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

import org.netkernel.layer0.representation.IHDSNode;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseAccessorImpl;
import org.netkernelroc.mod.layer2.DatabaseUtil;

public class PasswordAccessor extends DatabaseAccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    INKFResponse resp;

    String saltedPassword = PasswordUtil.generateSaltedPassword(util,
                                                                aContext.source("arg:password", String.class),
                                                                aContext.source("arg:id", String.class),
                                                                aContext.source("arg:siteSalt", String.class));

    String saltedSql= "SELECT id\n" +
                      "FROM   nk4um_user_account\n" +
                      "WHERE  id=?\n" +
                      "AND    password=?";


    if (util.issueSourceRequest("active:sqlPSBooleanQuery",
                                Boolean.class,
                                new ArgByValue("operand", saltedSql),
                                new ArgByValue("param", aContext.source("arg:id")),
                                new ArgByValue("param", saltedPassword))) {
      resp= aContext.createResponseFrom(true);
    } else {
      String unsaltedSql= "SELECT id\n" +
                          "FROM   nk4um_user_account\n" +
                          "WHERE  id=?\n" +
                          "AND    ( password=?\n" +
                          "     OR  password=?)";

      String legacyPassword = PasswordUtil.generateLegacyPassword(util, aContext.source("arg:password", String.class));
      String unsaltedPassword = PasswordUtil.generateUnsaltedPassword(util, aContext.source("arg:password", String.class));

      if (util.issueSourceRequest("active:sqlPSBooleanQuery",
                                  Boolean.class,
                                  new ArgByValue("operand", unsaltedSql),
                                  new ArgByValue("param", aContext.source("arg:id")),
                                  new ArgByValue("param", unsaltedPassword),
                                  new ArgByValue("param", legacyPassword))) {
        resp= aContext.createResponseFrom(true);

        // update users password hash
        PasswordUtil.updatePassword(util,
                                    aContext.source("arg:password", String.class),
                                    aContext.source("arg:id", String.class),
                                    aContext.source("arg:siteSalt", String.class));
      } else {
        resp= aContext.createResponseFrom(false);
      }
    }

    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
  
  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    PasswordUtil.updatePassword(util,
                                aContext.source("arg:password", String.class),
                                aContext.source("arg:id", String.class),
                                aContext.source("arg:siteSalt", String.class));
    util.cutGoldenThread("nk4um:user");
  }
}
