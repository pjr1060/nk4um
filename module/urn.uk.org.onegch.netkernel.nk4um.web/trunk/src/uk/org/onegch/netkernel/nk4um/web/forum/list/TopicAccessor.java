package uk.org.onegch.netkernel.nk4um.web.forum.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class TopicAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/forum/list/");
    
    IHDSNode topic= util.issueSourceRequest("nk4um:db:topic",
                                             IHDSNode.class,
                                             new Arg("id", "arg:id"));
    
    INKFRequest topicMetaReq= util.createSourceRequest("nk4um:db:topic:meta",
                                                       IHDSNode.class,
                                                       new Arg("id", "arg:id"));
    
    boolean moderator= aContext.exists("session:/currentUser") &&
                       util.issueExistsRequest("nk4um:db:forum:moderator",
                                               new ArgByValue("id", topic.getFirstValue("//forum_id")),
                                               new Arg("userId", "session:/currentUser"));
    
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "topic.xsl"),
                                      new Arg("operand", "topic.xml"),
                                      new ArgByValue("topic", topic),
                                      new ArgByRequest("topicMeta", topicMetaReq),
                                      new ArgByValue("moderator", moderator));
  }
}
