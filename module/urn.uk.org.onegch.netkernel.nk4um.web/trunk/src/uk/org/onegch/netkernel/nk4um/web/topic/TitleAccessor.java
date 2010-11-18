package uk.org.onegch.netkernel.nk4um.web.topic;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class TitleAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    IHDSNode userDetails= util.issueSourceRequest("nk4um:db:topic",
                                                  IHDSNode.class,
                                                  new Arg("id", "arg:id"));
    
    aContext.createResponseFrom(userDetails.getFirstValue("//title"));
  }
}
