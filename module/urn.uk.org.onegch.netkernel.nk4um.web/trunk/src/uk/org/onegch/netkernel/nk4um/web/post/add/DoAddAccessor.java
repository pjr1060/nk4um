package uk.org.onegch.netkernel.nk4um.web.post.add;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DoAddAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/post/add/");
    
    util.issueNewRequest("nk4um:db:post",
                         null,
                         null,
                         new Arg("topicId", "arg:topicId"),
                         new Arg("authorId", "session:/currentUser"),
                         new Arg("title", "httpRequest:/param/title"),
                         new Arg("content", "httpRequest:/param/content"));
    
    IHDSNode topicDetails= util.issueSourceRequest("nk4um:db:topic",
                                                   IHDSNode.class,
                                                   new Arg("id", "arg:topicId"));
    
    String title= "nk4um Reply Posted: " + topicDetails.getFirstValue("//title");
    
    IHDSNode userDetails= util.issueSourceRequest("nk4um:db:user",
                                                  IHDSNode.class,
                                                  new Arg("id", "session:/currentUser"));
    
    String url= aContext.source("httpRequest:/url", String.class);
    url= url.substring(0, url.indexOf("/nk4um/")) + "/nk4um/topic/" +
                aContext.source("arg:topicId") + "/index";
    
    INKFRequest contentReq= util.createSourceRequest("active:freemarker",
                                                     null,
                                                     new Arg("operator", "addEmailTemplate.txt"),
                                                     new ArgByValue("poster", userDetails.getFirstValue("//display_name")),
                                                     new Arg("title", "httpRequest:/param/title"),
                                                     new ArgByValue("topic", topicDetails.getFirstValue("//title")),
                                                     new ArgByValue("url", url),
                                                     new Arg("content", "httpRequest:/param/content"));
    
    INKFRequest notificationReq= util.createSinkRequest("nk4um:web:notification:send",
                                                        null,
                                                        new ArgByValue("forumId", topicDetails.getFirstValue("//forum_id")),
                                                        new Arg("authorId", "session:/currentUser"),
                                                        new ArgByValue("title", title),
                                                        new ArgByRequest("content", contentReq));
    aContext.issueAsyncRequest(notificationReq);
    
    aContext.sink("session:/message/class", "success");
    aContext.sink("session:/message/title", "Post successful");
    aContext.sink("session:/message/content", "Post successful :-)");
    
    aContext.sink("httpResponse:/redirect", "../index");
  }
}
