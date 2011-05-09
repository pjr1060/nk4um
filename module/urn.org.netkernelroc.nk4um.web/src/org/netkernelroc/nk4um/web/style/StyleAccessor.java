/*
 * Copyright (C) 2010-2011 by Chris Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.netkernelroc.nk4um.web.style;

import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.ByteArrayRepresentation;
import org.netkernel.layer0.representation.IBinaryStreamRepresentation;

import org.netkernel.xml.saxon.util.SaxonUtil;
import org.netkernelroc.mod.layer2.AccessorUtil;
import org.netkernelroc.mod.layer2.Arg;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.Layer2AccessorImpl;

import java.io.ByteArrayOutputStream;

public class StyleAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/org/netkernelroc/nk4um/web/style/");

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
