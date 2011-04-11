package uk.org.onegch.netkernel.nk4um.web.forum.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import uk.org.onegch.netkernel.layer2.*;

public class AtomForumAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/forum/list/");

    if (util.issueExistsRequest("nk4um:db:forum",
                                new Arg("id", "arg:id"))) {
      IHDSNode forum= util.issueSourceRequest("nk4um:db:forum",
                                              IHDSNode.class,
                                              new Arg("id", "arg:id"));

      IHDSNode forumMeta= util.issueSourceRequest("nk4um:db:forum:meta",
                                                  IHDSNode.class,
                                                  new Arg("id", "arg:id"));

      INKFRequest postListReq = util.createSourceRequest("nk4um:db:forum:postList",
                                                         IHDSNode.class,
                                                         new Arg("id", "arg:id"),
                                                         new ArgByValue("limit", 20));

      String url = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url") +
                   "forum/" + forum.getFirstValue("//id") + "/index";

      util.issueSourceRequestAsResponse("nk4um:web:atom",
                                        new ArgByValue("title", forum.getFirstValue("//title")),
                                        new ArgByValue("url", url),
                                        new ArgByRequest("postList", postListReq),
                                        new ArgByValue("lastPostTime", forumMeta.getFirstValue("//last_post_time")));
    } else {
      util.issueSourceRequestAsResponse("active:xrl2",
                                        new Arg("template", "forumNotFound.xml"));
      aContext.sink("httpResponse:/code", 404);
    }
  }
}
