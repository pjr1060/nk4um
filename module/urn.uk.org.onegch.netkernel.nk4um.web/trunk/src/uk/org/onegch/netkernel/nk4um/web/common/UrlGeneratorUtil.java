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
