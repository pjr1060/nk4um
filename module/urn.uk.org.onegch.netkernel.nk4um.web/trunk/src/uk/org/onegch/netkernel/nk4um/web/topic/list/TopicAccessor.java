package uk.org.onegch.netkernel.nk4um.web.topic.list;

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
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/topic/list/");
    
    
    if (util.issueExistsRequest("nk4um:db:topic",
                                new Arg("id", "arg:id"))) {
      IHDSNode topic= util.issueSourceRequest("nk4um:db:topic",
                                              IHDSNode.class,
                                              new Arg("id", "arg:id"));
      
      boolean moderator= aContext.exists("nk4um:security:currentUser") &&
                                         util.issueExistsRequest("nk4um:db:forum:moderator",
                                                                 new ArgByValue("id", topic.getFirstValue("//forum_id")),
                                                                 new Arg("userId", "nk4um:security:currentUser"));
      
      if (!(Boolean)topic.getFirstValue("//visible") && !moderator) {
        util.issueSourceRequestAsResponse("active:xrl2",
                                          new Arg("template", "topicNotFound.xml"));
        aContext.sink("httpResponse:/code", 404);
      } else {
        INKFRequest styleReq= util.createSourceRequest("active:xslt2",
                                                       null,
                                                       new Arg("operator", "topic.xsl"),
                                                       new Arg("operand", "topic.xml"),
                                                       new ArgByValue("topic", topic),
                                                       new ArgByValue("moderator", moderator));
        
        util.issueSourceRequestAsResponse("active:xrl2",
                                          new ArgByRequest("template", styleReq),
                                          new Arg("id", "arg:id"));
      }
    } else {
      util.issueSourceRequestAsResponse("active:xrl2",
                                        new Arg("template", "topicNotFound.xml"));
      aContext.sink("httpResponse:/code", 404);
    }
  }
}
