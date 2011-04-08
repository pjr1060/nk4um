package uk.org.onegch.netkernel.nk4um.web.topic.list;

import net.sf.saxon.s9api.XdmNode;
import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class PostAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/topic/list/");
    
    IHDSNode post= util.issueSourceRequest("nk4um:db:post",
                                           IHDSNode.class,
                                           new Arg("id", "arg:id"));

    INKFRequest userReq= util.createSourceRequest("nk4um:db:user",
                                                  IHDSNode.class,
                                                  new ArgByValue("id", post.getFirstValue("//author_id")));
    INKFRequest userMetaReq= util.createSourceRequest("nk4um:db:user:meta",
                                                      IHDSNode.class,
                                                      new ArgByValue("id", post.getFirstValue("//author_id")));

    Object postContent= util.issueSourceRequest("active:wikiParser/XHTML",
                                                  null,
                                                  new ArgByValue("operand", post.getFirstValue("//content")));

    postContent= util.issueSourceRequest("active:tagSoup",
                                         XdmNode.class,
                                         new ArgByValue("operand", postContent));
    
    boolean moderator= aContext.source("arg:displayModeration", Boolean.class) &&
                       aContext.exists("nk4um:security:currentUser") &&
                       util.issueExistsRequest("nk4um:db:forum:moderator",
                       new ArgByValue("id", post.getFirstValue("//forum_id")),
                       new Arg("userId", "nk4um:security:currentUser"));

    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "post.xsl"),
                                      new Arg("operand", "post.xml"),
                                      new ArgByValue("post", post),
                                      new ArgByValue("postContent", postContent),
                                      new ArgByRequest("user", userReq),
                                      new ArgByRequest("userMeta", userMetaReq),
                                      new ArgByValue("moderator", moderator));
  }
}
