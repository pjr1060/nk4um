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

import java.util.UUID;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseAccessorImpl;
import org.netkernelroc.mod.layer2.DatabaseUtil;

public class ActivateAccessor extends DatabaseAccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT     nk4um_user_account.id\n" +
                "FROM       nk4um_user_account\n" +
                "INNER JOIN nk4um_user_activation ON nk4um_user_activation.user_id=nk4um_user_account.id\n" +
                "WHERE      nk4um_user_account.email=?\n" +
                "AND        nk4um_user_activation.activation_code=?";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                         Boolean.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:email")),
                                                         new ArgByValue("param", aContext.source("arg:activationCode")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
  
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String updateSql= "UPDATE nk4um_user_account\n" +
                      "SET    activated='t'\n" +
                      "WHERE  email=?";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", updateSql),
                            new ArgByValue("param", aContext.source("arg:email")));
    String deleteSql= "DELETE \n" +
                      "FROM   nk4um_user_activation\n" +
                      "WHERE  user_id=(SELECT id\n" +
                      "                FROM   nk4um_user_account" +
                      "                WHERE  email=?)";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", deleteSql),
                            new ArgByValue("param", aContext.source("arg:email")));
    
    util.cutGoldenThread("nk4um:user");
  }
  
  @Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String disableSql= "UPDATE nk4um_user_account\n" +
                       "SET    activated='f'\n" +
                       "WHERE  id=?";
    
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", disableSql),
                            new ArgByValue("param", aContext.source("arg:id")));
    
    String uuid= UUID.randomUUID().toString();
    
    String clearActivationSql= "DELETE\n" +
                               "FROM   nk4um_user_activation\n" +
                               "WHERE  user_id=?";
    
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", clearActivationSql),
                            new ArgByValue("param", aContext.source("arg:id")));
    
    String accountActivationSql= "INSERT INTO  nk4um_user_activation (\n" +
                                 "      user_id,\n" +
                                 "      activation_code,\n" +
                                 "      creation_date\n" +
                                 ") VALUES (\n" +
                                 "      ?,\n" +
                                 "      ?,\n" +
                                 "      now())";
    
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", accountActivationSql),
                            new ArgByValue("param", aContext.source("arg:id")),
                            new ArgByValue("param", uuid));
    
    aContext.createResponseFrom(uuid.toString());
  }
}
