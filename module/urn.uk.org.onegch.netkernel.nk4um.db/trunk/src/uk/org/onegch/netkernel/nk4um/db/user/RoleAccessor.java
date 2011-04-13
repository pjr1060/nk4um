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

public class RoleAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String existsSql = "SELECT user_account_id FROM nk4um_user_meta WHERE user_account_id=?";

    String updateMetaSql;
    INKFResponse resp;
    if (util.issueSourceRequest("active:sqlPSBooleanQuery",
                                Boolean.class,
                                new ArgByValue("operand", existsSql),
                                new ArgByValue("param", aContext.source("arg:id")))) {
      String sql= "UPDATE nk4um_user_meta\n" +
                  "SET    role_name=?\n" +
                  "WHERE  user_account_id=?;";

      resp= util.issueSourceRequestAsResponse("active:sqlPSUpdate",
                                              new ArgByValue("operand", sql),
                                              new ArgByValue("param", aContext.sourcePrimary(String.class)),
                                              new ArgByValue("param", aContext.source("arg:id")));
    } else {
      String sql= "INSERT INTO nk4um_user_meta (display_name, user_account_id, role_name)\n" +
                  "VALUES                      ((SELECT email FROM nk4um_user WHERE id=?), ?, ?)";

      resp= util.issueSourceRequestAsResponse("active:sqlPSUpdate",
                                              new ArgByValue("operand", sql),
                                              new ArgByValue("param", aContext.source("arg:id")),
                                              new ArgByValue("param", aContext.source("arg:id")),
                                              new ArgByValue("param", aContext.sourcePrimary(String.class)));
    }

    resp.setHeader("no-cache", null);
    util.cutGoldenThread("nk4um:user");
  }
}
