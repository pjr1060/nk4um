package uk.org.onegch.netkernel.nk4um.db.forumGroup;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ModeratorListAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT     nk4um_user.id,\n" +
                "           nk4um_user.display_name,\n" +
                "           nk4um_user.email\n" +
                "FROM       nk4um_forum_group_moderator\n" +
                "INNER JOIN nk4um_user ON nk4um_user.id=nk4um_forum_group_moderator.user_id\n" +
                "WHERE      forum_group_id=?\n" +
                "ORDER BY   nk4um_user.display_name;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:forumGroup");
  }
}
