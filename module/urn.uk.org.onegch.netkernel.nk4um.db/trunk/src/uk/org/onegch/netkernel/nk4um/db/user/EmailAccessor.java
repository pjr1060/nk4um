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

public class EmailAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util)
      throws Exception {
    
    String email= util.escapeString(aContext.source("arg:email", String.class));
    
    String sql= "SELECT id\n" +
                "FROM   nk4um_user\n" +
                "WHERE  lower(email)=lower('" + email + "');";
    
    Long id= (Long)util.issueSourceRequest("active:sqlQuery",
                                           IHDSNode.class,
                                           new ArgByValue("operand", sql)).getFirstValue("//id");
    INKFResponse resp= aContext.createResponseFrom(id);
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util)
      throws Exception {

    String email= util.escapeString(aContext.source("arg:email", String.class));
    
    String sql= "SELECT id\n" +
                "FROM   nk4um_user\n" +
                "WHERE  lower(email)=lower('" + email + "');";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlBooleanQuery",
                                                         new ArgByValue("operand", sql));

    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
}
