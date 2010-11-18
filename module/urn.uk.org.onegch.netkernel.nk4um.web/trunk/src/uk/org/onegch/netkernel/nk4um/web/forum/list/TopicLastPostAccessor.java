package uk.org.onegch.netkernel.nk4um.web.forum.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class TopicLastPostAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/forum/list/");
    
    INKFRequest lastPostReq= util.createSourceRequest("nk4um:db:topic:lastPost",
                                                      IHDSNode.class,
                                                      new Arg("id", "arg:topicId"));
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "lastPost.xsl"),
                                      new Arg("operand", "lastPost.xml"),
                                      new ArgByRequest("lastPost", lastPostReq));
  }
}
