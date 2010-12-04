package uk.org.onegch.netkernel.nk4um.web.topic.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class PostAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/topic/list/");
    
    IHDSNode post= util.issueSourceRequest("nk4um:db:post",
                                           IHDSNode.class,
                                           new Arg("id", "arg:id"));

    INKFRequest userReq= util.createSourceRequest("nk4um:db:user",
                                                  IHDSNode.class,
                                                  new ArgByValue("id", post.getFirstValue("//author_id")));
    INKFRequest userMetaReq= util.createSourceRequest("nk4um:db:user:meta",
                                                      IHDSNode.class,
                                                      new ArgByValue("id", post.getFirstValue("//author_id")));
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "post.xsl"),
                                      new Arg("operand", "post.xml"),
                                      new ArgByValue("post", post),
                                      new ArgByRequest("user", userReq),
                                      new ArgByRequest("userMeta", userMetaReq));
  }
}
