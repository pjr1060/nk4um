package uk.org.onegch.netkernel.nk4um.web.author;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class DisplayNameAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    IHDSNode userDetails= util.issueSourceRequest("nk4um:db:user",
                                                  IHDSNode.class,
                                                  new Arg("id", "arg:id"));

    String displayName;
    if (userDetails.getFirstValue("//display_name") != null) {
      displayName = (String) userDetails.getFirstValue("//display_name");
    } else {
      displayName = (String) userDetails.getFirstValue("//email");
    }
    
    aContext.createResponseFrom(displayName);
  }
}
