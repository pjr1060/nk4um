package uk.org.onegch.netkernel.nk4um.web.post.preview;

import net.sf.saxon.s9api.XdmNode;
import org.netkernel.layer0.nkf.INKFRequestContext;
import uk.org.onegch.netkernel.layer2.*;

public class PreviewAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    Object resp= util.issueSourceRequest("active:wikiParser/XHTML",
                                         null,
                                         new ArgByValue("operand", aContext.source("httpRequest:/param/post", String.class)));

    resp= util.issueSourceRequest("active:tagSoup",
                                  XdmNode.class,
                                  new ArgByValue("operand", resp));

    aContext.createResponseFrom(resp);
  }
}
