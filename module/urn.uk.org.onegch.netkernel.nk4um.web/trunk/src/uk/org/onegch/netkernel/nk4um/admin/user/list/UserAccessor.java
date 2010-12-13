package uk.org.onegch.netkernel.nk4um.admin.user.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.w3c.dom.Document;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class UserAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/admin/user/list/");
    
    INKFRequest userReq= util.createSourceRequest("nk4um:db:user",
                                                  IHDSNode.class,
                                                  new Arg("id", "arg:id"));
    
    INKFRequest userMetaReq= util.createSourceRequest("nk4um:db:user:meta",
                                                      IHDSNode.class,
                                                      new Arg("id", "arg:id"));
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      Document.class,
                                      new Arg("operator", "user.xsl"),
                                      new ArgByRequest("operand", userReq),
                                      new ArgByRequest("userMeta", userMetaReq));
  }
}
