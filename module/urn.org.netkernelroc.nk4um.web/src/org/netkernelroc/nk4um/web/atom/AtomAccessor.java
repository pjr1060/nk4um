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

package org.netkernelroc.nk4um.web.atom;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;
import org.w3c.dom.Document;
import org.netkernelroc.mod.layer2.*;

public class AtomAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/org/netkernelroc/nk4um/web/atom/");

    INKFRequest styleReq= util.createSourceRequest("active:xslt2",
                                                   null,
                                                   new Arg("operator", "atom.xsl"),
                                                   new Arg("operand", "atom.xml"),
                                                   new Arg("title", "arg:title"),
                                                   new Arg("url", "arg:url"),
                                                   new Arg("postList", "arg:postList"));

    Object lastPostTime = aContext.source("arg:lastPostTime");
    if (lastPostTime != null) {
      styleReq.addArgumentByValue("lastPostTime", lastPostTime.toString());
    }

    INKFRequest xrlReq= util.createSourceRequest("active:xrl2",
                                                 null,
                                                 new ArgByRequest("template", styleReq));
    INKFResponse resp= util.issueSourceRequestAsResponse("active:xslt2",
                                                         Document.class,
                                                         new Arg("operator", "res:/org/netkernelroc/nk4um/web/style/tidyAtom.xsl"),
                                                         new ArgByRequest("operand", xrlReq));
    resp.setMimeType("application/atom+xml");
  }
}
