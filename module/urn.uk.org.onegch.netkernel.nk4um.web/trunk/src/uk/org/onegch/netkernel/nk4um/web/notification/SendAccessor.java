package uk.org.onegch.netkernel.nk4um.web.notification;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.NKFException;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

import java.util.ArrayList;
import java.util.List;

public class SendAccessor extends Layer2AccessorImpl {
  @Override
  public void onSink(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    List<Long> emailedPeople= new ArrayList<Long>();
    
    IHDSNode subscribers= util.issueSourceRequest("nk4um:db:forum:subscribed:list",
                                                  IHDSNode.class,
                                                  new Arg("forumId", "arg:forumId"));

    long userId= aContext.source("arg:authorId", Long.class);

    for (IHDSNode subscriber : subscribers.getNodes("//row")) {
      long subscriberUserId= (Long)subscriber.getFirstValue("user_id");
      if (subscriberUserId == userId) {
      } else {
        sendEmail(aContext, util, emailedPeople, subscriberUserId);
      }
    }

    if (aContext.exists("arg:topicId")) {
      IHDSNode posterList= util.issueSourceRequest("nk4um:db:topic:posterList",
                                                   IHDSNode.class,
                                                   new Arg("id", "arg:topicId"));

      for (IHDSNode poster : posterList.getNodes("//row")) {
        long subscriberUserId= (Long)poster.getFirstValue("author_id");

        if (subscriberUserId == userId) {
        } else {
          sendEmail(aContext, util, emailedPeople, subscriberUserId);
        }
      }
    }
  }

  private void sendEmail(INKFRequestContext aContext, AccessorUtil util, List<Long> emailedPeople, long subscriberUserId) throws NKFException {
    if (!emailedPeople.contains(subscriberUserId)) {
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
      emailedPeople.add(subscriberUserId);
    }
  }
}
