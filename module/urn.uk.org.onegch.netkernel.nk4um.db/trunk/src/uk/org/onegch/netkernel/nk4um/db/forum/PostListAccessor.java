package uk.org.onegch.netkernel.nk4um.db.forum;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class PostListAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String limitSql= "";
    if (aContext.exists("arg:limit")) {
      limitSql= "\nLIMIT " + aContext.source("arg:limit", Integer.class);
    }

    String sql= "SELECT     nk4um_visible_forum_topic_post.id,\n" +
                "           TRUE AS visible\n" +
                "FROM       nk4um_visible_forum_topic_post\n" +
                "INNER JOIN nk4um_visible_forum_topic ON nk4um_visible_forum_topic.id=nk4um_visible_forum_topic_post.forum_topic_id\n" +
                "WHERE      nk4um_visible_forum_topic.forum_id=?\n" +
                "ORDER BY   nk4um_visible_forum_topic_post.posted_date" +
                limitSql + ";";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));

    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:forum", "nk4um:topic", "nk4um:post");
  }
}