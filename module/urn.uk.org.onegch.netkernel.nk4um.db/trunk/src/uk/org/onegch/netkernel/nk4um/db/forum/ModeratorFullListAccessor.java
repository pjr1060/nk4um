package uk.org.onegch.netkernel.nk4um.db.forum;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ModeratorFullListAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String existsSql= "SELECT     nk4um_forum_moderator.user_id\n" +
                      "FROM       nk4um_forum\n" +
                      "INNER JOIN nk4um_forum_moderator ON nk4um_forum_moderator.forum_id=nk4um_forum.id\n" +
                      "WHERE      nk4um_forum.id=?\n" +
                      "UNION\n" +
                      "SELECT     nk4um_forum_group_moderator.user_id\n" +
                      "FROM       nk4um_forum\n" +
                      "INNER JOIN nk4um_forum_group_moderator ON nk4um_forum_group_moderator.forum_group_id=nk4um_forum.forum_group_id\n" +
                      "WHERE      nk4um_forum.id=?\n" +
                      "UNION\n" +
                      "SELECT     nk4um_user.id\n" +
                      "FROM       nk4um_user\n" +
                      "WHERE      nk4um_user.role_name='Administrator';";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         new ArgByValue("operand", existsSql),
                                                         new ArgByValue("param", aContext.source("arg:id")),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    
    util.attachGoldenThread("nk4um:forum", "nk4um:forumGroup");
  }
}
