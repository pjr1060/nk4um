package uk.org.onegch.netkernel.nk4um.web.forum;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

import java.util.ArrayList;
import java.util.List;

public class SubscribeAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    if (util.issueExistsRequest("nk4um:db:forum:subscription",
                                new Arg("userId", "nk4um:security:currentUser"),
                                new Arg("id", "arg:id"))) {
      // do nothing - already subscribed!
    } else {
      util.issueNewRequest("nk4um:db:forum:subscription",
                           null,
                           null,
                           new Arg("id", "arg:id"),
                           new Arg("userId", "nk4um:security:currentUser"));
    }

    aContext.sink("session:/message/class", "success");
    aContext.sink("session:/message/title", "Email subscription successfully");
    aContext.sink("session:/message/content", "You have successfully subscribed to this forum, you will now receive email updates.");
    aContext.sink("httpResponse:/redirect", "index");
  }

  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    onPost(aContext, util);
  }
}
