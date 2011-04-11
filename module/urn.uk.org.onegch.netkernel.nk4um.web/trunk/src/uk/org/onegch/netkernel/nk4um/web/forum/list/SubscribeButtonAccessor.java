package uk.org.onegch.netkernel.nk4um.web.forum.list;

import org.netkernel.layer0.nkf.INKFRequestContext;
import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class SubscribeButtonAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    if (aContext.exists("nk4um:security:currentUser")) {
      if (util.issueExistsRequest("nk4um:db:forum:subscription",
                                  new Arg("userId", "nk4um:security:currentUser"),
                                  new Arg("id", "arg:id"))) {
        aContext.createResponseFrom("<form method=\"POST\" action=\"doUnsubscribe\" style=\"display: inline\"><button type=\"submit\">Unsubscribe</button></form>");
      } else {
        aContext.createResponseFrom("<form method=\"POST\" action=\"doSubscribe\" style=\"display: inline\"><button type=\"submit\">Subscribe</button></form>");
      }
    } else {
      aContext.createResponseFrom("<span/>");
    }
  }
}
