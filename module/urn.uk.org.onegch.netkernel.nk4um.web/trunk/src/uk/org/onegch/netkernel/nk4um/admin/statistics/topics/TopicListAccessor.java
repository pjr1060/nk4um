package uk.org.onegch.netkernel.nk4um.admin.statistics.topics;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.w3c.dom.Document;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class TopicListAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/admin/statistics/topics/");
    
    INKFRequest topicListReq= util.createSourceRequest("nk4um:db:topic:list:all",
                                                       IHDSNode.class);
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      Document.class,
                                      new Arg("operator", "topicList.xsl"),
                                      new ArgByRequest("operand", topicListReq));
  }
}
