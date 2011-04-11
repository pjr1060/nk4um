package uk.org.onegch.netkernel.nk4um.web.forum;

import org.netkernel.layer0.nkf.INKFRequestContext;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class UnsubscribeAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    if (!util.issueExistsRequest("nk4um:db:forum:subscription",
                                 new Arg("userId", "nk4um:security:currentUser"),
                                 new Arg("id", "arg:id"))) {
      // do nothing - not subscribed!
    } else {
      util.issueDeleteRequest("nk4um:db:forum:subscription",
                              new Arg("id", "arg:id"),
                              new Arg("userId", "nk4um:security:currentUser"));
    }

    aContext.sink("session:/message/class", "success");
    aContext.sink("session:/message/title", "Email unsubscribed successfully");
    aContext.sink("session:/message/content", "You have successfully unsubscribed from this forum, you will now no longer receive email updates.");
    aContext.sink("httpResponse:/redirect", "index");
  }

  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    onPost(aContext, util);
  }
}
