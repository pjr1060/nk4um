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

import net.sf.saxon.s9api.XdmNode;
import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernelroc.mod.layer2.*;

public class AtomPostAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/org/netkernelroc/nk4um/web/atom/");
    
    IHDSNode post= util.issueSourceRequest("nk4um:db:post",
                                           IHDSNode.class,
                                           new Arg("id", "arg:id"));

    INKFRequest userReq= util.createSourceRequest("nk4um:db:user",
                                                  IHDSNode.class,
                                                  new ArgByValue("id", post.getFirstValue("//author_id")));
    INKFRequest userMetaReq= util.createSourceRequest("nk4um:db:user:meta",
                                                      IHDSNode.class,
                                                      new ArgByValue("id", post.getFirstValue("//author_id")));

    Object postContent= util.issueSourceRequest("active:wikiParser/XHTML",
                                                  null,
                                                  new ArgByValue("operand", post.getFirstValue("//content")));

    postContent= util.issueSourceRequest("active:tagSoup",
                                         XdmNode.class,
                                         new ArgByValue("operand", postContent));

    String url = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url") +
                   "topic/" + post.getFirstValue("//forum_topic_id") + "/index#" + post.getFirstValue("//id");
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "atomPost.xsl"),
                                      new Arg("operand", "atomPost.xml"),
                                      new ArgByValue("url", url),
                                      new ArgByValue("post", post),
                                      new ArgByValue("postContent", postContent),
                                      new ArgByRequest("user", userReq),
                                      new ArgByRequest("userMeta", userMetaReq));
  }
}
