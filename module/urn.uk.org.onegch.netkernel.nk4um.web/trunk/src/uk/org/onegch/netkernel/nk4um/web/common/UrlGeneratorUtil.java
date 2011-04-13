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

package uk.org.onegch.netkernel.nk4um.web.common;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.util.RequestBuilder;
import org.netkernel.layer0.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UrlGeneratorUtil {

  public static String dropScheme(String aIdentifier) {
    String result;
    int i = aIdentifier.indexOf(':');
    if (i >= 0) {
      result = aIdentifier.substring(i + 1);
    } else {
      result = aIdentifier;
    }
    return result;
  }


  public static String lookupMeta(INKFRequestContext aContext, String id) throws Exception {
    Document d = XMLUtils.newDocument();
    Element docEl = d.createElement("request");
    d.appendChild(docEl);
    Element idEl = d.createElement("identifier");
    XMLUtils.setText(idEl, "meta:" + id);
    docEl.appendChild(idEl);
    RequestBuilder b = new RequestBuilder(docEl, aContext.getKernelContext().getKernel().getLogger());
    INKFRequest req = b.buildRequest(aContext, null, null);
    return dropScheme(req.getIdentifier());
  }

  public static String lookup(INKFRequestContext aContext, String configEntry, String defaultUri) throws Exception {
    IHDSNode pdsState;

    try {
      pdsState= aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);
    } catch (Exception e) {
      throw new Exception("nk4um not initialized", e);
    }

    String url;
    if (pdsState.getFirstValue("//security_external") == null) {
      url = UrlGeneratorUtil.lookupMeta(aContext, defaultUri);
    } else {
      url = (String) pdsState.getFirstValue(configEntry);
    }

    return url;
  }
}
