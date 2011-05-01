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

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class UserAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT   id,\n" +
                "         email,\n" +
                "         display_name," +
                "         role_name,\n" +
                "         activated\n" +
                "FROM     nk4um_user\n" +
                "WHERE    id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
  
  @Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    IHDSNode details= aContext.sourcePrimary(IHDSNode.class);

    String nextIdSql= "SELECT nextval('nk4um_user_account_id_seq') AS id;";
    IHDSNode nextIdNode= util.issueSourceRequest("active:sqlPSQuery",
                                                 IHDSNode.class,
                                                 new ArgByValue("operand", nextIdSql));
    Long nextId= (Long)nextIdNode.getFirstValue("//id");

    String encryptedPassword= PasswordUtil.generateSaltedPassword(util,
                                                                  (String)details.getFirstValue("//password"),
                                                                  nextId + "",
                                                                  aContext.source("arg:siteSalt", String.class));

    String newUserSql= "INSERT INTO nk4um_user_account\n" +
                       "(\n" +
                       "    id,\n" +
                       "    password,\n" +
                       "    email,\n" +
                       "    joined_date\n" +
                       ") VALUES (\n" +
                       "    ?,\n" +
                       "    ?,\n" +
                       "    ?,\n" +
                       "    NOW()\n" +
                       ");";
    
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", newUserSql),
                            new ArgByValue("param", nextId),
                            new ArgByValue("param", encryptedPassword),
                            new ArgByValue("param", details.getFirstValue("//email")));
    
    String newUserMetaSql= "INSERT INTO nk4um_user_meta\n" +
                           "(\n" +
                           "    user_account_id,\n" +
                           "    display_name\n" +
                           ") VALUES (\n" +
                           "    ?,\n" +
                           "    ?\n" +
                           ");";
    
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", newUserMetaSql),
                            new ArgByValue("param", nextId),
                            new ArgByValue("param", details.getFirstValue("//display")));
    
    aContext.createResponseFrom(nextId);
    
    util.cutGoldenThread("nk4um:user");
  }
  
  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    IHDSNode details= aContext.sourcePrimary(IHDSNode.class);
    String updateAccountSql= "UPDATE nk4um_user_account\n" +
                             "SET    email=?\n" +
                             "WHERE  id=?";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", updateAccountSql),
                            new ArgByValue("param", details.getFirstValue("//email")),
                            new ArgByValue("param", aContext.source("arg:id")));
    
    util.cutGoldenThread("nk4um:user");
  }
}
