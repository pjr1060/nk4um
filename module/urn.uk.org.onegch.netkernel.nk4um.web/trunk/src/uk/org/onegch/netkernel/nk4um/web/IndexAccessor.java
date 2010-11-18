package uk.org.onegch.netkernel.nk4um.web;

import net.sf.saxon.s9api.XdmNode;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IBinaryStreamRepresentation;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class IndexAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/index/");
    
    INKFRequest forumGroupsReq= util.createSourceRequest("active:xslt2",
                                                         XdmNode.class,
                                                         new Arg("operator", "forumGroups.xsl"),
                                                         new Arg("operand", "forumGroups.xml"),
                                                         new Arg("forumGroup", "forumGroup.xml"),
                                                         new Arg("forumGroups", "nk4um:db:forumGroups"));
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:xrl2",
                                                         IBinaryStreamRepresentation.class,
                                                         new ArgByRequest("template", forumGroupsReq));
    
    resp.setMimeType("text/html");
  }
}
