package uk.org.onegch.netkernel.nk4um.web;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import uk.org.onegch.netkernel.layer2.*;

public class AtomIndexAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    IHDSNode meta= util.issueSourceRequest("nk4um:db:meta",
                                           IHDSNode.class);

    INKFRequest postListReq = util.createSourceRequest("nk4um:db:post:listAll",
                                                       IHDSNode.class);

    String url = (String) aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url");

    util.issueSourceRequestAsResponse("nk4um:web:atom",
                                      new ArgByValue("title", "nk4um"),
                                      new ArgByValue("url", url),
                                      new ArgByRequest("postList", postListReq),
                                      new ArgByValue("lastPostTime", meta.getFirstValue("//last_post_time")));
  }
}
