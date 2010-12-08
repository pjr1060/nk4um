package uk.org.onegch.netkernel.nk4um.db.forum;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class BreadcrumbsAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT     nk4um_forum_group.id    AS forum_group_id,\n" +
                "           nk4um_forum_group.title AS forum_group_title,\n" +
                "           nk4um_forum.id          AS forum_id,\n" +
                "           nk4um_forum.title       AS forum_title\n" +
                "FROM       nk4um_forum\n" +
                "INNER JOIN nk4um_forum_group       ON nk4um_forum_group.id=nk4um_forum.forum_group_id\n" +
                "WHERE      nk4um_forum.id=?;";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:forum", "nk4um:forumGroup");
  }
}
