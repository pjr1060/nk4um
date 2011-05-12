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

package org.netkernelroc.nk4um.web.forum.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernelroc.mod.layer2.*;

public class AtomForumAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/org/netkernelroc/nk4um/web/forum/list/");

    if (util.issueExistsRequest("nk4um:db:forum",
                                new Arg("id", "arg:id"))) {
      IHDSNode forum= util.issueSourceRequest("nk4um:db:forum",
                                              IHDSNode.class,
                                              new Arg("id", "arg:id"));

      IHDSNode forumMeta= util.issueSourceRequest("nk4um:db:forum:meta",
                                                  IHDSNode.class,
                                                  new Arg("id", "arg:id"));

      IHDSNode postList = util.issueSourceRequest("nk4um:db:forum:postList",
                                                  IHDSNode.class,
                                                  new Arg("id", "arg:id"),
                                                  new ArgByValue("limit", 20));

      String url = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url") +
                   "forum/" + forum.getFirstValue("//id") + "/index";

      util.issueSourceRequestAsResponse("nk4um:web:atom",
                                        new ArgByValue("title", forum.getFirstValue("//title")),
                                        new ArgByValue("url", url),
                                        new ArgByValue("postList", postList),
                                        new ArgByValue("lastPostTime", forumMeta.getFirstValue("//last_post_time")));
    } else {
      util.issueSourceRequestAsResponse("active:xrl2",
                                        new Arg("template", "forumNotFound.xml"));
      aContext.sink("httpResponse:/code", 404);
    }
  }
}
