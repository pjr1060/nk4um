package uk.org.onegch.netkernel.nk4um.admin.forumGroup;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class DoEditAccessor extends Layer2AccessorImpl
{
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    //nk4um:db:forumGroup
    util.issueSinkRequest("nk4um:db:forumGroup",
                          null, 
                          new Arg("id", "arg:id"),
                          new Arg("order", "httpRequest:/param/order"),
                          new Arg("title", "httpRequest:/param/title"),
                          new Arg("description", "httpRequest:/param/description"));
    
    aContext.sink("httpResponse:/redirect", "edit");
  }
}
