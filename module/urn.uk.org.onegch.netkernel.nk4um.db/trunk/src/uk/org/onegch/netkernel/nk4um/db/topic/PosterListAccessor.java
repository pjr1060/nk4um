package uk.org.onegch.netkernel.nk4um.db.topic;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class PosterListAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT DISTINCT\n" +
                "       author_id\n" +
                "FROM   nk4um_forum_topic_post\n" +
                "WHERE  forum_topic_id=?";

    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));

    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:topic", "nk4um:post");
  }
}