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

package uk.org.onegch.netkernel.nk4um.admin.forumGroup;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class DoEditAccessor extends Layer2AccessorImpl
{
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    //nk4um:db:forumGroup
    util.issueSinkRequest("nk4um:db:forumGroup",
                          null, 
                          new Arg("id", "arg:id"),
                          new Arg("order", "httpRequest:/param/order"),
                          new Arg("title", "httpRequest:/param/title"),
                          new Arg("description", "httpRequest:/param/description"));
    
    aContext.sink("httpResponse:/redirect", "edit");
  }
}