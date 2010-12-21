package uk.org.onegch.netkernel.nk4um.web.notification;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class SendAccessor extends Layer2AccessorImpl {
  @Override
  public void onSink(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    IHDSNode subscribers= util.issueSourceRequest("nk4um:db:forum:subscribed:list",
                                                  IHDSNode.class,
                                                  new Arg("forumId", "arg:forumId"));
    
    long userId= aContext.source("arg:authorId", Long.class);
    
    for (IHDSNode subscriber : subscribers.getNodes("//row")) {
      long subscriberUserId= (Long)subscriber.getFirstValue("user_id");
      if (subscriberUserId == userId) {
      } else {
        IHDSNode userDetails= util.issueSourceRequest("nk4um:db:user",
                                                      IHDSNode.class,
                                                      new ArgByValue("id", subscriberUserId));
        
        HDSBuilder headerBuilder= new HDSBuilder();
        headerBuilder.pushNode("email");
        headerBuilder.addNode("from", "nk4um@1gch.co.uk");
        headerBuilder.addNode("to", userDetails.getFirstValue("//email"));
        headerBuilder.addNode("subject", aContext.source("arg:title"));
        
        util.issueSourceRequest("active:sendmail",
                                null,
                                new ArgByValue("header", headerBuilder.getRoot()),
                                new Arg("body", "arg:content"));
        
      }
    }
  }
}
