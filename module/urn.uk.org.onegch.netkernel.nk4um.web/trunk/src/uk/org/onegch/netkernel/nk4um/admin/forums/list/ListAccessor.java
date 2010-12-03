package uk.org.onegch.netkernel.nk4um.admin.forums.list;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.w3c.dom.Document;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class ListAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/admin/forums/list/");
    
    INKFRequest styledReq= util.createSourceRequest("active:xslt2",
                                                    Document.class,
                                                    new Arg("operator", "forumGroups.xsl"),
                                                    new Arg("operand", "nk4um:db:forumGroup:list"));
    
    util.issueSourceRequestAsResponse("active:xrl2",
                                      new ArgByRequest("template", styledReq));
  }
}
