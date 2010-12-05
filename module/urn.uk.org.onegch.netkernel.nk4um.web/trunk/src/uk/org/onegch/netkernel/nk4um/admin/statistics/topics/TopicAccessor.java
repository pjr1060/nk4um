package uk.org.onegch.netkernel.nk4um.admin.statistics.topics;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.w3c.dom.Document;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class TopicAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/admin/statistics/topics/");
    
    INKFRequest topicReq= util.createSourceRequest("nk4um:db:topic",
                                                   IHDSNode.class,
                                                   new Arg("id", "arg:id"));
    
    INKFRequest topicMetaReq= util.createSourceRequest("nk4um:db:topic:meta",
                                                       IHDSNode.class,
                                                       new Arg("id", "arg:id"));
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      Document.class,
                                      new Arg("operator", "topic.xsl"),
                                      new ArgByRequest("operand", topicReq),
                                      new ArgByRequest("topicMeta", topicMetaReq));
  }
}
