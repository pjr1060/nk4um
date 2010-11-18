package uk.org.onegch.netkernel.nk4um.web.forum;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class BreadcrumbsAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/forum/");
    
    INKFRequest breadcrumbsReq= util.createSourceRequest("nk4um:db:forum:breadcrumbs",
                                                   IHDSNode.class,
                                                   new Arg("id", "arg:id"));
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "breadcrumbs.xsl"),
                                      new Arg("operand", "breadcrumbs.xml"),
                                      new ArgByRequest("breadcrumbs", breadcrumbsReq));
  }
}
