package uk.org.onegch.netkernel.nk4um.web.post.preview;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.w3c.dom.Document;
import uk.org.onegch.netkernel.layer2.*;

public class PreviewAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    util.issueSourceRequestAsResponse("active:wikiParser/XHTML",
                                      new Arg("operand", "httpRequest:/param/post"));

  }
}
