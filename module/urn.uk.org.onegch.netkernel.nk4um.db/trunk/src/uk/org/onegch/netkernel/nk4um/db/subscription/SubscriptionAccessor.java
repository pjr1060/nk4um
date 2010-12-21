package uk.org.onegch.netkernel.nk4um.db.subscription;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class SubscriptionAccessor extends DatabaseAccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT id\n" +
                "FROM   nk4um_forum_subscription\n" +
                "WHERE  user_id=?\n" +
                "AND    forum_id=?\n" +
                "AND    notification_type_name='email'";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                         Boolean.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:userId")),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:forum", "nk4um:user", "nk4um:subscription");
  }
  
  @Override
  public void onDelete(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "DELETE\n" +
                "FROM   nk4um_forum_subscription\n" +
                "WHERE  user_id=?\n" +
                "AND    forum_id=?\n" +
                "AND    notification_type_name='email';";

    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", sql),
                            new ArgByValue("param", aContext.source("arg:userId")),
                            new ArgByValue("param", aContext.source("arg:id")));
    
    util.cutGoldenThread("nk4um:subscription");
  }
  
  @Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "INSERT INTO nk4um_forum_subscription\n" +
                "(\n" +
                "    user_id,\n" +
                "    forum_id,\n" +
                "    notification_type_name\n" +
                ") VALUES (\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    'email'\n" +
                ");";
    
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", sql),
                            new ArgByValue("param", aContext.source("arg:userId")),
                            new ArgByValue("param", aContext.source("arg:id")));
    
    util.cutGoldenThread("nk4um:subscription");
  }
}
