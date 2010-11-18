package uk.org.onegch.netkernel.nk4um.web.style;

import net.sf.saxon.s9api.XdmNode;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IBinaryStreamRepresentation;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class StyleAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/style/");

    XdmNode template= util.issueSourceRequest("active:xslt2",
                                              XdmNode.class,
                                              new Arg("operator", "style.xsl"),
                                              new Arg("operand", "template.xml"),
                                              new Arg("content", "arg:operand"));
    XdmNode xrl= util.issueSourceRequest("active:xrl2",
                                         XdmNode.class,
                                         new ArgByValue("template", template));
    INKFResponse resp= util.issueSourceRequestAsResponse("active:xslt2",
                                                         IBinaryStreamRepresentation.class,
                                                         new Arg("operator", "tidyHtml.xsl"),
                                                         new ArgByValue("operand", xrl));
    resp.setMimeType("text/html");
  }
}
