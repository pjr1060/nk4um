package uk.org.onegch.netkernel.nk4um.web.atom;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;
import org.w3c.dom.Document;
import uk.org.onegch.netkernel.layer2.*;

public class AtomAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/atom/");

    INKFRequest styleReq= util.createSourceRequest("active:xslt2",
                                                   null,
                                                   new Arg("operator", "atom.xsl"),
                                                   new Arg("operand", "atom.xml"),
                                                   new Arg("title", "arg:title"),
                                                   new Arg("url", "arg:url"),
                                                   new Arg("postList", "arg:postList"));

    String lastPostTime = aContext.source("arg:lastPostTime").toString();
    if (lastPostTime != null) {
      styleReq.addArgumentByValue("lastPostTime", lastPostTime);
    }

    INKFRequest xrlReq= util.createSourceRequest("active:xrl2",
                                                 null,
                                                 new ArgByRequest("template", styleReq));
    INKFResponse resp= util.issueSourceRequestAsResponse("active:xslt2",
                                                         Document.class,
                                                         new Arg("operator", "res:/uk/org/onegch/netkernel/nk4um/web/style/tidyAtom.xsl"),
                                                         new ArgByRequest("operand", xrlReq));
    resp.setMimeType("application/atom+xml");
  }
}
