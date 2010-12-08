package uk.org.onegch.netkernel.nk4um.db.forum;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class LastPostAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT     nk4um_forum_topic.id,\n" +
                "           nk4um_forum_topic.title,\n" +
                "           nk4um_forum_topic_post.author_id,\n" +
                "           nk4um_forum_topic_post.posted_date\n" +
                "FROM       nk4um_forum_topic\n" +
                "INNER JOIN nk4um_forum_topic_post ON nk4um_forum_topic_post.forum_topic_id=nk4um_forum_topic.id\n" +
                "WHERE      nk4um_forum_topic.forum_id=?\n" +
                "ORDER BY   posted_date DESC\n" +
                "LIMIT      1;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:forum", "nk4um:topic", "nk4um:post");
  }
}
