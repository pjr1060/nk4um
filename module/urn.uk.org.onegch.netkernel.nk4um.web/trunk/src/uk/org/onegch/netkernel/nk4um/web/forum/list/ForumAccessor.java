package uk.org.onegch.netkernel.nk4um.web.forum.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class ForumAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/forum/list/");
    
    if (util.issueExistsRequest("nk4um:db:forum",
                                new Arg("id", "arg:id"))) {
      INKFRequest forumReq= util.createSourceRequest("nk4um:db:forum",
                                                     IHDSNode.class,
                                                     new Arg("id", "arg:id"));
      
      INKFRequest styleReq= util.createSourceRequest("active:xslt2",
                                                     null,
                                                     new Arg("operator", "forum.xsl"),
                                                     new Arg("operand", "forum.xml"),
                                                     new ArgByRequest("forum", forumReq));
      
      util.issueSourceRequestAsResponse("active:xrl2",
                                        new ArgByRequest("template", styleReq),
                                        new Arg("id", "arg:id"));
    } else {
      util.issueSourceRequestAsResponse("active:xrl2",
                                        new Arg("template", "forumNotFound.xml"));
      aContext.sink("httpResponse:/code", 404);
    }}
}
