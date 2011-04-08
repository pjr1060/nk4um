package uk.org.onegch.netkernel.nk4um.web.topic.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.*;

public class PostListAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/topic/list/");
    
    INKFRequest postListReq= util.createSourceRequest("nk4um:db:post:list",
                                                       IHDSNode.class,
                                                       new Arg("topicId", "arg:id"));
    
    IHDSNode post= util.issueSourceRequest("nk4um:db:post",
                                           IHDSNode.class,
                                           new Arg("id", "arg:id"));

    boolean moderator= aContext.source("arg:displayModeration", Boolean.class) &&
                       aContext.exists("nk4um:security:currentUser") &&
                       util.issueExistsRequest("nk4um:db:forum:moderator",
                       new ArgByValue("id", post.getFirstValue("//forum_id")),
                       new Arg("userId", "nk4um:security:currentUser"));
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "postList.xsl"),
                                      new Arg("operand", "postList.xml"),
                                      new Arg("displayModeration", "arg:displayModeration"),
                                      new ArgByRequest("postList", postListReq),
                                      new ArgByValue("moderator", moderator));
  }
}
