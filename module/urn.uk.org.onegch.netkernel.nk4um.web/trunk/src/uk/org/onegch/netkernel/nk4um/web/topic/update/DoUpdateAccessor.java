package uk.org.onegch.netkernel.nk4um.web.topic.update;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;
import uk.org.onegch.netkernel.layer2.PrimaryArg;

public class DoUpdateAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    String url;
    if (aContext.exists("httpRequest:/header/Referer") &&
        aContext.source("httpRequest:/header/Referer", String.class).contains("/nk4um/") &&
        !aContext.source("httpRequest:/header/Referer", String.class).contains("doUpdate")) {
      url= aContext.source("httpRequest:/header/Referer", String.class);
    } else {
      url= "/nk4um/";
    }
    
    IHDSNode topic= util.issueSourceRequest("nk4um:db:topic",
                                            IHDSNode.class,
                                            new Arg("id", "arg:id"));
    
    if(aContext.exists("nk4um:security:currentUser") &&
                       util.issueExistsRequest("nk4um:db:forum:moderator",
                                               new ArgByValue("id", topic.getFirstValue("//forum_id")),
                                               new Arg("userId", "nk4um:security:currentUser"))) {
      util.issueSinkRequest("nk4um:db:topic:status",
                            new PrimaryArg("httpRequest:/param/status"),
                            new Arg("id", "arg:id"));
      
      aContext.sink("session:/message/class", "success");
      aContext.sink("session:/message/title", "Topic status updated");
      aContext.sink("session:/message/content", "The topic status has been updated.");
    } else {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "Topic status not changed");
      aContext.sink("session:/message/content", "You are not a moderator for this forum, and so cannot change the status of the topic.");
    }
    aContext.sink("httpResponse:/redirect", url);
    
  }
}
