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

import org.json.JSONArray;
import org.json.JSONObject;
import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.IHDSNodeList;
import org.netkernelroc.mod.layer2.AccessorUtil;
import org.netkernelroc.mod.layer2.Arg;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.Layer2AccessorImpl;

import java.util.List;

public class UserPageAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    INKFRequest filteredConfigReq = util.createSourceRequest("nk4um:dataTable:columns",
                                                            IHDSNode.class);

    INKFRequest allConfigReq = util.createSourceRequest("nk4um:dataTable:columns",
                                                        IHDSNode.class);

    String search = aContext.source("httpRequest:/param/sSearch", String.class);
    if (search != null && !search.trim().equals("")) {
      filteredConfigReq.addArgumentByValue("search", search);
    }

    IHDSNode filteredConfig = (IHDSNode) aContext.issueRequest(filteredConfigReq);
    IHDSNode allConfig = (IHDSNode) aContext.issueRequest(allConfigReq);

    IHDSNodeList allRowList = util.issueSourceRequest("nk4um:db:user:list",
                                                      IHDSNode.class,
                                                      new ArgByValue("config", allConfig)).getNodes("//row");

    IHDSNodeList filterRowList = util.issueSourceRequest("nk4um:db:user:list",
                                                         IHDSNode.class,
                                                         new ArgByValue("config", filteredConfig)).getNodes("//row");


    JSONObject jsonObject = new JSONObject();
    jsonObject.put("sEcho", aContext.source("httpRequest:/param/sEcho", Integer.class));
    jsonObject.put("iTotalRecords", allRowList.size());
    jsonObject.put("iTotalDisplayRecords", filterRowList.size());

    List<JSONArray> jsonRowList = util.issueSourceRequest("active:java",
                                                          List.class,
                                                          new Arg("class", "org.netkernelroc.nk4um.admin.user.list.UserPageArrayAccessor"),
                                                          new ArgByValue("iDisplayStart", aContext.source("httpRequest:/param/iDisplayStart")),
                                                          new ArgByValue("iDisplayLength", aContext.source("httpRequest:/param/iDisplayLength")),
                                                          new ArgByValue("iSortingCols", aContext.source("httpRequest:/param/iSortingCols")),
                                                          new ArgByValue("iSortCol0", aContext.source("httpRequest:/param/iSortCol_0")),
                                                          new ArgByValue("sSortDir0", aContext.source("httpRequest:/param/sSortDir_0")),
                                                          new ArgByValue("sSearch", aContext.source("httpRequest:/param/sSearch")));
    jsonObject.put("aaData", jsonRowList);

    aContext.createResponseFrom(jsonObject.toString());
  }
}
