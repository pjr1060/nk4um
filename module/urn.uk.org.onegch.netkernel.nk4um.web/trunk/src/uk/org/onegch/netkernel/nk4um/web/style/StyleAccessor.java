package uk.org.onegch.netkernel.nk4um.web.style;

import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.ByteArrayRepresentation;
import org.netkernel.layer0.representation.IBinaryStreamRepresentation;

import org.netkernel.xml.saxon.util.SaxonUtil;
import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

import java.io.ByteArrayOutputStream;

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
    XdmNode tidied= util.issueSourceRequest("active:xslt2",
                                            XdmNode.class,
                                            new Arg("operator", "tidyHtml.xsl"),
                                            new ArgByValue("operand", xrl));

    Serializer serializer = new Serializer();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
    serializer.setOutputStream(baos);
    serializer.setOutputProperty(Serializer.Property.DOCTYPE_PUBLIC, "-//W3C//DTD HTML 4.01 Transitional//EN");
    serializer.setOutputProperty(Serializer.Property.DOCTYPE_SYSTEM, "http://www.w3.org/TR/html4/loose.dtd");
    serializer.setOutputProperty(Serializer.Property.INDENT, "yes");
    SaxonUtil.getProcessor().writeXdmValue(tidied, serializer);

    aContext.createResponseFrom(new ByteArrayRepresentation(baos)).setMimeType("text/html");
  }
}
