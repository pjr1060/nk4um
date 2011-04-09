package uk.org.onegch.netkernel.nk4um.web.topic.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.*;

public class PostListAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/topic/list/");

    INKFRequest postListReq;
    boolean moderator;

    if (aContext.exists("arg:id")) {
      postListReq = util.createSourceRequest("nk4um:db:post:list",
                                             IHDSNode.class,
                                             new Arg("topicId", "arg:id"));

      IHDSNode topic = util.issueSourceRequest("nk4um:db:topic",
                                               IHDSNode.class,
                                               new Arg("id", "arg:id"));

      moderator = aContext.source("arg:displayModeration", Boolean.class) &&
              aContext.exists("nk4um:security:currentUser") &&
              util.issueExistsRequest("nk4um:db:forum:moderator",
                                      new ArgByValue("id", topic.getFirstValue("//forum_id")),
                                      new Arg("userId", "nk4um:security:currentUser"));
    } else {
      postListReq = util.createSourceRequest("nk4um:db:post:moderationList",
                                             IHDSNode.class);
      moderator = true;
    }

    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "postList.xsl"),
                                      new Arg("operand", "postList.xml"),
                                      new Arg("displayModeration", "arg:displayModeration"),
                                      new ArgByRequest("postList", postListReq),
                                      new ArgByValue("moderator", moderator));
  }
}
