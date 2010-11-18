package uk.org.onegch.netkernel.nk4um.web.post.add;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DoAddAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    util.issueNewRequest("nk4um:db:post",
                         null,
                         null,
                         new Arg("topicId", "arg:topicId"),
                         new Arg("authorId", "session:/currentUser"),
                         new Arg("title", "httpRequest:/param/title"),
                         new Arg("content", "httpRequest:/param/content"));

    aContext.sink("session:/message/class", "success");
    aContext.sink("session:/message/title", "Post successful");
    aContext.sink("session:/message/content", "Post successful :-)");
    
    aContext.sink("httpResponse:/redirect", "../index");
  }
}
