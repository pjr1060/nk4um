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

package uk.org.onegch.netkernel.nk4um.web.exception;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.WrappedThrowable;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.ExceptionUtil;

public class PageNotFoundAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.sink("session:/message/class", "error");
    aContext.sink("session:/message/title", "Page not found");
    aContext.sink("session:/message/content", "The requested page doesn't exist.");
    
    aContext.logRaw(INKFRequestContext.LEVEL_WARNING, "Page not found: " + aContext.getThisRequest().getIdentifier());
    
    util.issueSourceRequestAsResponse("active:java",
                                      new Arg("class", "uk.org.onegch.netkernel.nk4um.web.style.StyleAccessor"),
                                      new Arg("operand", "res:/uk/org/onegch/netkernel/nk4um/web/exception/error.xml"));
    aContext.sink("httpResponse:/code", 404);
  }
}
