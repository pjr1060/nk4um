package uk.org.onegch.netkernel.nk4um.db.forum;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class MetaAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT    ( SELECT count(nk4um_forum_topic.id)\n" +
                "            FROM   nk4um_forum_topic\n" +
                "            WHERE  nk4um_forum_topic.forum_id=nk4um_forum.id)\n" +
                "            AS topic_count,\n" +
                "          ( SELECT     count(nk4um_forum_topic_post.id)\n" +
                "            FROM       nk4um_forum_topic\n" +
                "            INNER JOIN nk4um_forum_topic_post ON nk4um_forum_topic_post.forum_topic_id=nk4um_forum_topic.id\n" +
                "            WHERE      nk4um_forum_topic.forum_id=nk4um_forum.id)\n" +
                "            AS post_count,\n" +
                "          ( SELECT     count(nk4um_topic_view.id)\n" +
                "            FROM       nk4um_forum_topic\n" +
                "            INNER JOIN nk4um_topic_view ON nk4um_topic_view.topic_id=nk4um_forum_topic.id\n" +
                "            WHERE      nk4um_forum_topic.forum_id=nk4um_forum.id)\n" +
                "            AS view_count\n" +
                "FROM      nk4um_forum\n" +
                "WHERE     nk4um_forum.id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:forum", "nk4um:topic", "nk4um:post");
  }
}
