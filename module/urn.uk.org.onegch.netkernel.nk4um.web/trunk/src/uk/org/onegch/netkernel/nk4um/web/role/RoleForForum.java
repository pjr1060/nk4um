package uk.org.onegch.netkernel.nk4um.web.role;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class RoleForForum extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    IHDSNode user= util.issueSourceRequest("nk4um:db:user",
                                           IHDSNode.class,
                                           new Arg("id", "arg:userId"));
    
    boolean moderator= util.issueExistsRequest("nk4um:db:forum:moderator",
                                               new Arg("id", "arg:forumId"),
                                               new Arg("userId", "arg:userId"));
    
    String roleForForum;
    if (user.getFirstValue("//role_name").equals("Administrator")) {
      roleForForum= "nk4um " + user.getFirstValue("//role_name");
    } else if (moderator) {
      roleForForum= "nk4um Moderator";
    } else {
      roleForForum= "nk4um " + user.getFirstValue("//role_name");
    }
    
    aContext.createResponseFrom(roleForForum);
  }
}
