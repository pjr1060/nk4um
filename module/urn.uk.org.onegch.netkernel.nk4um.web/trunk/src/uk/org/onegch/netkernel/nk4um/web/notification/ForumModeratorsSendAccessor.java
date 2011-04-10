package uk.org.onegch.netkernel.nk4um.web.notification;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class ForumModeratorsSendAccessor extends Layer2AccessorImpl {
  @Override
  public void onSink(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    IHDSNode subscribers= util.issueSourceRequest("nk4um:db:forum:moderator:fullList",
                                                  IHDSNode.class,
                                                  new Arg("id", "arg:forumId"));
    
    for (IHDSNode subscriber : subscribers.getNodes("//row")) {
      long subscriberUserId= (Long)subscriber.getFirstValue("user_id");
      IHDSNode userDetails= util.issueSourceRequest("nk4um:db:user",
                                                    IHDSNode.class,
                                                    new ArgByValue("id", subscriberUserId));

      HDSBuilder headerBuilder= new HDSBuilder();
      headerBuilder.pushNode("email");
      headerBuilder.addNode("to", userDetails.getFirstValue("//email"));
      headerBuilder.addNode("subject", aContext.source("arg:title"));

      util.issueSourceRequest("active:sendmail",
                              null,
                              new ArgByValue("header", headerBuilder.getRoot()),
                              new Arg("body", "arg:content"));
    }
  }
}
