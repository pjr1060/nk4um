package uk.org.onegch.netkernel.nk4um.admin.statistics.forumGroups;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.w3c.dom.Document;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class ForumGroupAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/admin/statistics/forumGroups/");
    
    INKFRequest forumGroupReq= util.createSourceRequest("nk4um:db:forumGroup",
                                                        IHDSNode.class,
                                                        new Arg("id", "arg:id"));
    
    INKFRequest forumGroupMetaReq= util.createSourceRequest("nk4um:db:forumGroup:meta",
                                                            IHDSNode.class,
                                                            new Arg("id", "arg:id"));
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      Document.class,
                                      new Arg("operator", "forumGroup.xsl"),
                                      new ArgByRequest("operand", forumGroupReq),
                                      new ArgByRequest("forumGroupMeta", forumGroupMetaReq));
  }
}
