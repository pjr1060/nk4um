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

package org.netkernelroc.nk4um.web.topic.list;

import net.sf.saxon.s9api.XdmNode;
import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import org.netkernelroc.mod.layer2.Arg;
import org.netkernelroc.mod.layer2.ArgByRequest;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.HttpLayer2AccessorImpl;
import org.netkernelroc.mod.layer2.HttpUtil;

public class PostAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/org/netkernelroc/nk4um/web/topic/list/");
    
    IHDSNode post= util.issueSourceRequest("nk4um:db:post",
                                           IHDSNode.class,
                                           new Arg("id", "arg:id"));

    INKFRequest userReq= util.createSourceRequest("nk4um:db:user",
                                                  IHDSNode.class,
                                                  new ArgByValue("id", post.getFirstValue("//author_id")));
    INKFRequest userMetaReq= util.createSourceRequest("nk4um:db:user:meta",
                                                      IHDSNode.class,
                                                      new ArgByValue("id", post.getFirstValue("//author_id")));
    Object postContent;
    if ((Boolean) post.getFirstValue("//legacy")) {
      postContent = post.getFirstValue("//content");
    } else {
      postContent= util.issueSourceRequest("active:wikiParser/XHTML",
                                           null,
                                           new ArgByValue("operand", post.getFirstValue("//content")));
    }

    boolean canEdit;
    if ((Boolean) post.getFirstValue("//legacy")) {
      canEdit = false;
    } else if (aContext.source("arg:moderator", Boolean.class) ||
               post.getFirstValue("//author_id").equals(aContext.source("nk4um:security:currentUser", Long.class))) {
      canEdit = true;
    } else {
      canEdit = false;
    }


    postContent= util.issueSourceRequest("active:tagSoup",
                                         XdmNode.class,
                                         new ArgByValue("operand", postContent));
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "post.xsl"),
                                      new Arg("operand", "post.xml"),
                                      new ArgByValue("post", post),
                                      new ArgByValue("postContent", postContent),
                                      new ArgByRequest("user", userReq),
                                      new ArgByRequest("userMeta", userMetaReq),
                                      new Arg("moderator", "arg:moderator"),
                                      new ArgByValue("canEdit", canEdit));
  }
}
