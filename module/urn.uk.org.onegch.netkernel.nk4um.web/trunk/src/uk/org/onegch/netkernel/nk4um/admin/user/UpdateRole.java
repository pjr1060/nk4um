package uk.org.onegch.netkernel.nk4um.admin.user;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;
import uk.org.onegch.netkernel.layer2.PrimaryArg;

public class UpdateRole extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    util.issueSinkRequest("nk4um:db:user:role",
                          new PrimaryArg("httpRequest:/param/role"),
                          new Arg("id", "arg:id"));
    
    aContext.sink("httpResponse:/redirect", "/nk4um/users/");
  }
}
