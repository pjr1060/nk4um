package uk.org.onegch.netkernel.nk4um.db.forumGroup;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ForumGroupAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    
    String sql= "SELECT   id,\n" +
                "         title,\n" +
                "         description\n" +
                "FROM     nk4um_forum_group\n" +
                "WHERE    id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new Arg("param", "arg:id"));
    
    resp.setHeader("no-cache", null);
    resp.setExpiry(INKFResponse.EXPIRY_NEVER);
    util.attachGoldenThread("nk4um:all", "nk4um:forumGroup");
  }
}
