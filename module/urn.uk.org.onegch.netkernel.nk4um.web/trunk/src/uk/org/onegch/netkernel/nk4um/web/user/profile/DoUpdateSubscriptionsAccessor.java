package uk.org.onegch.netkernel.nk4um.web.user.profile;

import java.util.ArrayList;
import java.util.List;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DoUpdateSubscriptionsAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    IHDSNode currentUserSubscriptions= util.issueSourceRequest("nk4um:db:forum:subscription:list",
                                                               IHDSNode.class,
                                                               new Arg("userId", "session:/currentUser"));
    
    List<Long> currentSubscriptionIds= new ArrayList<Long>();
    
    for (IHDSNode row : currentUserSubscriptions.getNodes("//row")) {
      currentSubscriptionIds.add((Long)row.getFirstValue("forum_id"));
    }
    
    IHDSNode paramsNode= aContext.source("httpRequest:/params",
                                         IHDSNode.class);
    
    for (IHDSNode param : paramsNode.getNodes("/*")) {
      String paramName= param.getName();
      if (paramName.startsWith("forum_")) {
        long forumId= Long.parseLong(paramName.substring(paramName.indexOf("forum_") + "forum_".length()));
        if (currentSubscriptionIds.contains(forumId)) {
          currentSubscriptionIds.remove(forumId);
        } else {
          util.issueNewRequest("nk4um:db:forum:subscription",
                               null,
                               null,
                               new ArgByValue("id", forumId),
                               new Arg("userId", "session:/currentUser"));
        }
      }
    }
    
    // any remaining subscriptions have been removed
    for (Long forumId : currentSubscriptionIds) {
      util.issueDeleteRequest("nk4um:db:forum:subscription",
                              new ArgByValue("id", forumId),
                              new Arg("userId", "session:/currentUser"));
    }
    
    aContext.sink("session:/message/class", "success");
    aContext.sink("session:/message/title", "Update Forum Subscriptions: success");
    aContext.sink("session:/message/content", "Your forum subscriptions have been updated");
    aContext.sink("httpResponse:/redirect", "profile");
  }
}
