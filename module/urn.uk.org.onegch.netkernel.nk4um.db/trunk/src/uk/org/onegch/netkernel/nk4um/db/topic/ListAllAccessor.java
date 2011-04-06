package uk.org.onegch.netkernel.nk4um.db.topic;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ListAllAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT     id," +
                "           nk4um_topic_status.visible\n" +
                "FROM       nk4um_forum_topic\n" +
                "INNER JOIN nk4um_topic_status ON nk4um_topic_status.status=nk4um_forum_topic.status\n" +
                "ORDER BY   nk4um_topic_status.display_order,\n" +
                "           posted_date DESC;";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:topic");
  }
}
