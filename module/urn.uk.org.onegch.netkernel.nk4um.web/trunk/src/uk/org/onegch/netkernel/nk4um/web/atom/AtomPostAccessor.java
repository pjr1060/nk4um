package uk.org.onegch.netkernel.nk4um.web.atom;

import net.sf.saxon.s9api.XdmNode;
import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import uk.org.onegch.netkernel.layer2.*;

public class AtomPostAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/atom/");
    
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

    String url = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url") +
                   "topic/" + post.getFirstValue("//forum_topic_id") + "/index#" + post.getFirstValue("//id");
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "atomPost.xsl"),
                                      new Arg("operand", "atomPost.xml"),
                                      new ArgByValue("url", url),
                                      new ArgByValue("post", post),
                                      new ArgByValue("postContent", postContent),
                                      new ArgByRequest("user", userReq),
                                      new ArgByRequest("userMeta", userMetaReq));
  }
}
