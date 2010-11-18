package uk.org.onegch.netkernel.nk4um.web.style;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.nkf.INKFResponseReadOnly;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgFromResponse;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class AutoStyleAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    @SuppressWarnings("rawtypes")
    INKFResponseReadOnly originalResp= aContext.sourceForResponse("arg:response");
    boolean autoStyle= originalResp.hasHeader("nk4umAutoStyle");
    
    if (autoStyle) {
      util.issueSourceRequestAsResponse("active:java",
                                        new Arg("class", "uk.org.onegch.netkernel.nk4um.web.style.StyleAccessor"),
                                        new ArgFromResponse("operand", originalResp));
    } else {
      INKFResponse resp= aContext.createResponseFrom(aContext.source("arg:response"));
      resp.setMimeType(originalResp.getMimeType());
    }
    
  }
}
