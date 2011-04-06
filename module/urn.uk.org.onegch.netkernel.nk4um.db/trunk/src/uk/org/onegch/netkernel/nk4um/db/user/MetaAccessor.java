package uk.org.onegch.netkernel.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class MetaAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT    ( SELECT     count(nk4um_forum_topic_post.id)\n" +
                "            FROM       nk4um_forum_topic_post\n" +
                "            WHERE      nk4um_forum_topic_post.author_id=nk4um_user.id)\n" +
                "            AS post_count\n" +
                "FROM      nk4um_user\n" +
                "WHERE     nk4um_user.id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user", "nk4um:post");
  }

  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    IHDSNode details= aContext.sourcePrimary(IHDSNode.class);

    System.out.println(details);

    String updateMetaSql= "UPDATE nk4um_user_meta\n" +
                          "SET    display_name=?\n" +
                          "WHERE  user_account_id=?";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", updateMetaSql),
                            new ArgByValue("param", details.getFirstValue("//display_name")),
                            new ArgByValue("param", aContext.source("arg:id")));

    util.cutGoldenThread("nk4um:user");
  }
}
