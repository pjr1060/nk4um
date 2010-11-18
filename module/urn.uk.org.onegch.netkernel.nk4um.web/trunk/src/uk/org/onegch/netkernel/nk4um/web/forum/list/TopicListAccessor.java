package uk.org.onegch.netkernel.nk4um.web.forum.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class TopicListAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/forum/list/");

    INKFRequest topicListReq= util.createSourceRequest("nk4um:db:topic:list",
                                                       IHDSNode.class,
                                                       new Arg("forumId", "arg:id"));

    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "topicList.xsl"),
                                      new Arg("operand", "topicList.xml"),
                                      new ArgByRequest("topicList", topicListReq));
  }
}
