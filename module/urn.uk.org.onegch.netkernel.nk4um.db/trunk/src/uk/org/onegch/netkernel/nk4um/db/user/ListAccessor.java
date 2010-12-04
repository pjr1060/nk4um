package uk.org.onegch.netkernel.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ListAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT   id,\n" +
                "         username,\n" +
                "         email,\n" +
                "         display_name,\n" +
                "         activated,\n" +
                "         ( SELECT     count(nk4um_forum_topic_post.id)\n" +
                "           FROM       nk4um_forum_topic_post\n" +
                "           WHERE      nk4um_forum_topic_post.author_id=nk4um_user.id)\n" +
                "           AS post_count\n" +
                "FROM     nk4um_user;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
}
