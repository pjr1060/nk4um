package uk.org.onegch.netkernel.nk4um.web.forumGroup.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class ForumGroupAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/forumGroup/list/");
    
    INKFRequest forumGroupReq= util.createSourceRequest("nk4um:db:forumGroup",
                                                        IHDSNode.class,
                                                        new Arg("id", "arg:id"));
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "forumGroup.xsl"),
                                      new Arg("operand", "forumGroup.xml"),
                                      new ArgByRequest("forumGroup", forumGroupReq));
  }
}
