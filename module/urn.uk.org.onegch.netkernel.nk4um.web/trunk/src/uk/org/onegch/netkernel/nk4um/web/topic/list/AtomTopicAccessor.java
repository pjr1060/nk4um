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

      String url = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url") +
                   "topic/" + topic.getFirstValue("//id") + "/index";

      INKFRequest styleReq= util.createSourceRequest("active:xslt2",
                                                     null,
                                                     new Arg("operator", "topic.xsl"),
                                                     new Arg("operand", "atom.xml"),
                                                     new ArgByValue("topic", topic),
                                                     new ArgByValue("topicMeta", topicMeta),
                                                     new ArgByValue("url", url));

      INKFRequest xrlReq= util.createSourceRequest("active:xrl2",
                                                   null,
                                                   new ArgByRequest("template", styleReq),
                                                   new Arg("id", "arg:id"));
      INKFResponse resp= util.issueSourceRequestAsResponse("active:xslt2",
                                                           Document.class,
                                                           new Arg("operator", "res:/uk/org/onegch/netkernel/nk4um/web/style/tidyAtom.xsl"),
                                                           new ArgByRequest("operand", xrlReq));
      resp.setMimeType("application/atom+xml");
    } else {
      util.issueSourceRequestAsResponse("active:xrl2",
                                        new Arg("template", "topicNotFound.xml"));
      aContext.sink("httpResponse:/code", 404);
    }
  }
}
