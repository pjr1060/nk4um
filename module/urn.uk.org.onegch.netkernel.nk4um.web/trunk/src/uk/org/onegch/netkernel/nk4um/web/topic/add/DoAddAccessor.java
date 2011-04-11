package uk.org.onegch.netkernel.nk4um.web.topic.add;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import org.netkernel.layer0.representation.impl.HDSBuilder;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DoAddAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/topic/add/");

    String title= aContext.source("httpRequest:/param/title", String.class).trim();
    String content= aContext.source("httpRequest:/param/content", String.class).trim();

    boolean valid= true;
    HDSBuilder reasonsBuilder= new HDSBuilder();
    reasonsBuilder.pushNode("div");
    reasonsBuilder.addNode("p", "New topic failed for the following reasons: ");
    reasonsBuilder.pushNode("ul");

    if (title.equals("")) {
      valid= false;
      reasonsBuilder.addNode("li", "Title must be supplied");
    }

    if (content.equals("")) {
      valid= false;
      reasonsBuilder.addNode("li", "Content must be supplied");
    }

    if (valid) {
      String status= "active";

      boolean moderation= aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//new_user_moderated") != null &&
                                    !util.issueExistsRequest("nk4um:db:user:activePosts",
                                                             new Arg("id", "nk4um:security:currentUser"));
      if (moderation) {
        status= "moderation";
      }

      long topicId= util.issueNewRequest("nk4um:db:topic",
                                         Long.class,
                                         null,
                                         new Arg("forumId", "arg:forumId"),
                                         new Arg("authorId", "nk4um:security:currentUser"),
                                         new ArgByValue("title", title),
                                         new ArgByValue("content", content),
                                         new ArgByValue("postStatus", status));

      IHDSNode topicDetails= util.issueSourceRequest("nk4um:db:topic",
                                                     IHDSNode.class,
                                                     new ArgByValue("id", topicId));

      String emailTitle = "nk4um New Topic: " + topicDetails.getFirstValue("//title");

      IHDSNode userDetails= util.issueSourceRequest("nk4um:db:user",
                                                    IHDSNode.class,
                                                    new ArgByValue("id", topicDetails.getFirstValue("//author_id")));

      String url= (String) aContext.source("fpds:/nk4um/config.xml", IHDSNode.class).getFirstValue("//base_url") +
                  "topic/" + topicId + "/index";

      String displayName;
      if (userDetails.getFirstValue("//display_name") != null) {
        displayName = (String) userDetails.getFirstValue("//display_name");
      } else {
        displayName = (String) userDetails.getFirstValue("//email");
      }

      if (moderation) {
        String moderatorEmailContent= "User " + userDetails.getFirstValue("//display_name") + " has added a new topic which is pending moderation." +
                                      "To moderate visit " + url + " (The message won't be visible until you login)\n\nContent:\n" +
                                      content;

        INKFRequest notifyModerators= util.createSinkRequest("nk4um:web:notification:send:forumModerators",
                                                             null,
                                                             new Arg("forumId", "arg:forumId"),
                                                             new ArgByValue("title", "nk4um Pending Moderation: " + topicDetails.getFirstValue("//title")),
                                                             new ArgByValue("content", moderatorEmailContent));

        aContext.issueAsyncRequest(notifyModerators);

        aContext.sink("session:/message/class", "info");
        aContext.sink("session:/message/title", "Topic awaiting moderation");
        aContext.sink("session:/message/content", "As a new user your post has been passed on to a moderator for approval. " +
                                                  "Once you have had a post approved, you'll be able to post freely.");
      } else {
        INKFRequest contentReq= util.createSourceRequest("active:freemarker",
                                                         null,
                                                         new Arg("operator", "addEmailTemplate.txt"),
                                                         new ArgByValue("poster", displayName),
                                                         new ArgByValue("forum", topicDetails.getFirstValue("//forum")),
                                                         new ArgByValue("topic", topicDetails.getFirstValue("//title")),
                                                         new ArgByValue("url", url),
                                                         new ArgByValue("content", content));

        INKFRequest notificationReq= util.createSinkRequest("nk4um:web:notification:send",
                                                            null,
                                                            new Arg("forumId", "arg:forumId"),
                                                            new Arg("authorId", "nk4um:security:currentUser"),
                                                            new ArgByValue("title", emailTitle),
                                                            new ArgByRequest("content", contentReq));
        aContext.issueAsyncRequest(notificationReq);

        aContext.sink("session:/message/class", "success");
        aContext.sink("session:/message/title", "Post Topic successful");
        aContext.sink("session:/message/content", "Post Topic successful :-)");
      }

      aContext.sink("httpResponse:/redirect", "../index");
    } else {
      aContext.sink("session:/message/class", "error");
      aContext.sink("session:/message/title", "New topic failure");
      aContext.sink("session:/message/content", reasonsBuilder.getRoot());

      if (aContext.exists("httpRequest:/params")) {
        aContext.sink("session:/formData/name", "addTopic");
        aContext.sink("session:/formData/params", aContext.source("httpRequest:/params"));
      }

      aContext.sink("httpResponse:/redirect", "add");
    }
  }
}
