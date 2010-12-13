package uk.org.onegch.netkernel.nk4um.admin.forumGroup;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class DoAddModeratorAccessor extends Layer2AccessorImpl
{
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    if (util.issueExistsRequest("nk4um:db:user:email",
                                new Arg("email", "httpRequest:/param/email"))) {
      INKFRequest userIdRequest= util.createSourceRequest("nk4um:db:user:email",
                                                          null,
                                                          new Arg("email", "httpRequest:/param/email"));
      
      util.issueNewRequest("nk4um:db:forumGroup:moderator",
                           null,
                           null,
                           new Arg("id", "arg:id"),
                           new ArgByRequest("userId", userIdRequest));
    } else {
      throw new Exception("No user with this email address");
    }
    
    aContext.sink("httpResponse:/redirect", "../edit");
  }
}
