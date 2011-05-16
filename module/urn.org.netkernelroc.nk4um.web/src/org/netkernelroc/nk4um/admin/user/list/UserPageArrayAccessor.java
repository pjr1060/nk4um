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

package org.netkernelroc.nk4um.admin.user.list;

import net.sf.saxon.s9api.XdmNode;
import org.json.JSONArray;
import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.IHDSNodeList;
import org.netkernelroc.mod.layer2.*;
import org.netkernelroc.nk4um.web.forum.list.TopicListAccessor;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class UserPageArrayAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/org/netkernelroc/nk4um/admin/user/list/");
    
    INKFRequest displayConfigReq = util.createSourceRequest("nk4um:dataTable:columns",
                                                            IHDSNode.class,
                                                            new ArgByValue("start", aContext.source("arg:iDisplayStart")),
                                                            new ArgByValue("length", aContext.source("arg:iDisplayLength")));

    if (aContext.source("arg:iSortingCols", Integer.class) == 1) {
      displayConfigReq.addArgumentByValue("sortColumn", getColumnName(aContext.source("arg:iSortCol0", Integer.class)));
      displayConfigReq.addArgumentByValue("sortDirection", aContext.source("arg:sSortDir0", String.class));
    }

    String search = aContext.source("arg:sSearch", String.class);
    if (search != null && !search.trim().equals("")) {
      displayConfigReq.addArgumentByValue("search", search);
    }

    IHDSNode displayConfig = (IHDSNode) aContext.issueRequest(displayConfigReq);

    IHDSNodeList displayRowList = util.issueSourceRequest("nk4um:db:user:list",
                                                          IHDSNode.class,
                                                          new ArgByValue("config", displayConfig)).getNodes("//row");

    // id, post_date, title
    List<JSONArray> jsonRowList = new ArrayList<JSONArray>();
    for (IHDSNode row : displayRowList) {
      IHDSNode user = util.issueSourceRequest("nk4um:db:user",
                                              IHDSNode.class,
                                              new ArgByValue("id", row.getFirstValue("id")));

      Object[] rowArray = new Object[8];

      rowArray[0] = row.getFirstValue("display_name");
      rowArray[1] = row.getFirstValue("email");
      rowArray[2] = user.getFirstValue("//joined_date");
      rowArray[3] = row.getFirstValue("post_count");
      rowArray[4] = row.getFirstValue("activated");
      rowArray[5] = user.getFirstValue("//status");
      rowArray[6] = user.getFirstValue("//role_name");
      rowArray[7] = util.issueSourceRequest("active:xslt2",
                                            XdmNode.class,
                                            new Arg("operator", "userUpdate.xsl"),
                                            new ArgByValue("operand", user)).toString();
      jsonRowList.add(new JSONArray(rowArray));
    }

    aContext.createResponseFrom(jsonRowList);
  }

  public static String getColumnName(Integer column) throws Exception {
    if (column == 0) {
      return "display_name";
    } else if (column == 1) {
      return "email";
    } else if (column == 2) {
      return "joined_date";
    } else if (column == 3) {
      return "( SELECT     count(id)\n" +
             "  FROM       nk4um_visible_quick_forum_topic_post\n" +
             "  WHERE      nk4um_visible_quick_forum_topic_post.author_id=nk4um_user.id)";
    } else if (column == 4) {
      return "activated";
    } else if (column == 5) {
      return "status";
    } else if (column == 6) {
      return "role_name";
    } else if (column == 7) {
      return "role_name";
    } else {
      throw new Exception("Unexpected column number " + column);
    }
  }
}
