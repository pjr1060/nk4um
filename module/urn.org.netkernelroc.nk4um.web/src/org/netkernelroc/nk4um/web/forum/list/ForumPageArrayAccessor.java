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

import net.sf.saxon.s9api.XdmNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.IHDSNodeList;
import org.netkernelroc.mod.layer2.*;

import java.util.ArrayList;
import java.util.List;

public class ForumPageArrayAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/org/netkernelroc/nk4um/web/forum/list/");
    
    boolean moderator= aContext.exists("nk4um:security:currentUser") &&
                       util.issueExistsRequest("nk4um:db:forum:moderator",
                                               new Arg("id", "arg:id"),
                                               new Arg("userId", "nk4um:security:currentUser"));
    
    INKFRequest displayConfigReq = util.createSourceRequest("nk4um:dataTable:columns",
                                                            IHDSNode.class,
                                                            new ArgByValue("start", aContext.source("arg:iDisplayStart")),
                                                            new ArgByValue("length", aContext.source("arg:iDisplayLength")),
                                                            new ArgByValue("moderator", moderator));

    if (aContext.source("arg:iSortingCols", Integer.class) == 1) {
      displayConfigReq.addArgumentByValue("sortColumn", TopicListAccessor.getColumnName(aContext.source("arg:iSortCol0", Integer.class)));
      displayConfigReq.addArgumentByValue("sortDirection", aContext.source("arg:sSortDir0", String.class));
    }

    String search = aContext.source("arg:sSearch", String.class);
    if (search != null && !search.trim().equals("")) {
      displayConfigReq.addArgumentByValue("search", search);
    }

    IHDSNode displayConfig = (IHDSNode) aContext.issueRequest(displayConfigReq);

    IHDSNodeList displayRowList = util.issueSourceRequest("nk4um:db:topic:list",
                                                          IHDSNode.class,
                                                          new Arg("forumId", "arg:id"),
                                                          new ArgByValue("config", displayConfig)).getNodes("//row");

    // id, post_date, title
    List<JSONArray> jsonRowList = new ArrayList<JSONArray>();
    for (IHDSNode row : displayRowList) {
      Object[] rowArray;
      if (moderator) {
        rowArray = new Object[5];
      } else {
        rowArray = new Object[4];
      }

      INKFRequest lastPostReq = util.createSourceRequest("nk4um:web:forum:topicLastPost",
                                                         XdmNode.class,
                                                         new ArgByValue("topicId", row.getFirstValue("id")));

      IHDSNode topic= util.issueSourceRequest("nk4um:db:topic",
                                              IHDSNode.class,
                                              new ArgByValue("id", row.getFirstValue("id")));

      IHDSNode topicMeta= util.issueSourceRequest("nk4um:db:topic:meta",
                                                  IHDSNode.class,
                                                  new ArgByValue("id", row.getFirstValue("id")));

      String classString = "topic-" + topic.getFirstValue("//status");
      rowArray[0] = "<div class=\"" + classString + "\"><a href=\"/nk4um/topic/" + row.getFirstValue("id") + "/\">" + topic.getFirstValue("//title") +  "</a></div>";
      rowArray[1] = topicMeta.getFirstValue("//post_count");
      rowArray[2] = util.issueSourceRequest("nk4um:db:topic:view",
                                            Long.class,
                                            new ArgByValue("id", row.getFirstValue("id")));
      rowArray[3] = util.issueSourceRequest("active:xrl2",String.class, new ArgByRequest("template", lastPostReq));
      if (moderator) {
        XdmNode moderatorCell = util.issueSourceRequest("active:xslt2",
                                                        XdmNode.class,
                                                        new Arg("operator", "moderatorCell.xsl"),
                                                        new Arg("operand", "moderatorCell.xml"),
                                                        new ArgByValue("topic", topic));
        rowArray[4] = moderatorCell.toString();
      }
      
      jsonRowList.add(new JSONArray(rowArray));
    }

    aContext.createResponseFrom(jsonRowList);
  }
}
