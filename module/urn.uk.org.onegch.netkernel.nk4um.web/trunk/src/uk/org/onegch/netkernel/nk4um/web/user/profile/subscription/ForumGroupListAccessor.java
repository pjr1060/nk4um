package uk.org.onegch.netkernel.nk4um.web.user.profile.subscription;

import net.sf.saxon.s9api.XdmNode;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class ForumGroupListAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/user/profile/subscription/");
    
    INKFRequest forumGroupsReq= util.createSourceRequest("active:xslt2",
                                                         XdmNode.class,
                                                         new Arg("operator", "forumGroupList.xsl"),
                                                         new Arg("operand", "forumGroupList.xml"),
                                                         new Arg("forumGroups", "nk4um:db:forumGroup:list"));
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:xrl2",
                                                         new ArgByRequest("template", forumGroupsReq));
    
    resp.setMimeType("text/html");
  }
}
