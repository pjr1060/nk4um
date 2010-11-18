package uk.org.onegch.netkernel.nk4um.web.topic.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class TopicAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/topic/list/");
    
    INKFRequest topicReq= util.createSourceRequest("nk4um:db:topic",
                                                   IHDSNode.class,
                                                   new Arg("id", "arg:id"));
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "topic.xsl"),
                                      new Arg("operand", "topic.xml"),
                                      new ArgByRequest("topic", topicReq));
  }
}
