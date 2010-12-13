package uk.org.onegch.netkernel.nk4um.db.forumGroup;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ModeratorAccessor extends DatabaseAccessorImpl {
@Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String insertModeratorSql= "INSERT INTO nk4um_forum_group_moderator\n" +
                               "(\n" +
                               "    forum_group_id,\n" +
                               "    user_id\n" +
                               ") VALUES (\n" +
                               "    ?,\n" +
                               "    ?\n" +
                                ");";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", insertModeratorSql),
                            new ArgByValue("param", aContext.source("arg:id")),
                            new ArgByValue("param", aContext.source("arg:userId")));
    
    util.cutGoldenThread("nk4um:forumGroup");
  }
  
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String existsSql= "SELECT     nk4um_forum_group_moderator.user_id\n" +
                      "FROM       nk4um_forum\n" +
                      "INNER JOIN nk4um_forum_group_moderator ON nk4um_forum_group_moderator.forum_group_id=nk4um_forum.forum_group_id\n" +
                      "WHERE      nk4um_forum.id=?\n" +
                      "AND        nk4um_forum_group_moderator.user_id=?;";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                         new ArgByValue("operand", existsSql),
                                                         new ArgByValue("param", aContext.source("arg:id")),
                                                         new ArgByValue("param", aContext.source("arg:userId")));
    
    resp.setHeader("no-cache", null);
    
    util.attachGoldenThread("nk4um:forum", "nk4um:forumGroup");
  }
}
