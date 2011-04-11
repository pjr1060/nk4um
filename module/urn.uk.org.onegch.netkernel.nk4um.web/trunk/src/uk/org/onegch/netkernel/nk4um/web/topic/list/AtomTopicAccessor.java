package uk.org.onegch.netkernel.nk4um.web.topic.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;
import org.w3c.dom.Document;
import uk.org.onegch.netkernel.layer2.*;

public class AtomTopicAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/topic/list/");
    
    if (util.issueExistsRequest("nk4um:db:topic",
                                new Arg("id", "arg:id"))) {
      IHDSNode topic= util.issueSourceRequest("nk4um:db:topic",
                                              IHDSNode.class,
                                              new Arg("id", "arg:id"));

      IHDSNode topicMeta= util.issueSourceRequest("nk4um:db:topic:meta",
                                                  IHDSNode.class,
                                                  new Arg("id", "arg:id"));

      INKFRequest postListReq = util.createSourceRequest("nk4um:db:post:list",
                                                         IHDSNode.class,
                                                         new Arg("topicId", "arg:id"),
                                                         new ArgByValue("limit", 20));

      String url = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url") +
                   "topic/" + topic.getFirstValue("//id") + "/index";

      util.issueSourceRequestAsResponse("nk4um:web:atom",
                                        new ArgByValue("title", topic.getFirstValue("//title")),
                                        new ArgByValue("url", url),
                                        new ArgByRequest("postList", postListReq),
                                        new ArgByValue("lastPostTime", topicMeta.getFirstValue("//last_post_time")));
    } else {
      util.issueSourceRequestAsResponse("active:xrl2",
                                        new Arg("template", "topicNotFound.xml"));
      aContext.sink("httpResponse:/code", 404);
    }
  }
}
