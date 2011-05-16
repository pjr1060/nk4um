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

package org.netkernelroc.nk4um.admin.user;

import org.netkernel.layer0.nkf.INKFRequestContext;

import org.netkernelroc.mod.layer2.Arg;
import org.netkernelroc.mod.layer2.HttpLayer2AccessorImpl;
import org.netkernelroc.mod.layer2.HttpUtil;
import org.netkernelroc.mod.layer2.PrimaryArg;

public class UpdateAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    util.issueSinkRequest("nk4um:db:user:role",
                          new PrimaryArg("httpRequest:/param/role"),
                          new Arg("id", "arg:id"));
    util.issueSinkRequest("nk4um:db:user:status",
                          new PrimaryArg("httpRequest:/param/status"),
                          new Arg("id", "arg:id"));
    
    aContext.sink("httpResponse:/redirect", "/nk4um/users/");
  }
}
