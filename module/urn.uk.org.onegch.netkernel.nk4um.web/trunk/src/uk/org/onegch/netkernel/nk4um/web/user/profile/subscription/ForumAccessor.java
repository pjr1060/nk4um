package uk.org.onegch.netkernel.nk4um.web.user.profile.subscription;

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
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/user/profile/subscription/");
    
    INKFRequest forumReq= util.createSourceRequest("nk4um:db:forum",
                                                   IHDSNode.class,
                                                   new Arg("id", "arg:id"));
    
    INKFRequest subscriptionReq= util.createExistsRequest("nk4um:db:forum:subscription",
                                                          new Arg("id", "arg:id"),
                                                          new Arg("userId", "session:/currentUser"));
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "forum.xsl"),
                                      new Arg("operand", "forum.xml"),
                                      new ArgByRequest("forum", forumReq),
                                      new ArgByRequest("subscribed", subscriptionReq));
  }
}