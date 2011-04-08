package uk.org.onegch.netkernel.nk4um.db.post;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ListAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT     id,\n" +
                "           nk4um_post_status.visible\n" +
                "FROM       nk4um_forum_topic_post\n" +
                "INNER JOIN nk4um_post_status ON nk4um_post_status.status=nk4um_forum_topic_post.status\n" +
                "WHERE      forum_topic_id=?\n" +
                "ORDER BY   posted_date;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:topicId")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:topic", "nk4um:post");
  }
}
