package uk.org.onegch.netkernel.nk4um.db.post;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class StatusAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "UPDATE nk4um_forum_topic_post\n" +
                "SET    status=?\n" +
                "WHERE  id=?\n";
    
    util.issueSourceRequest("active:sqlPSUpdate",
                            IHDSNode.class,
                            new ArgByValue("operand", sql),
                            new ArgByValue("param", aContext.sourcePrimary(String.class)),
                            new ArgByValue("param", aContext.source("arg:id")));
    
    util.cutGoldenThread("nk4um:topic", "nk4um:post");
  }
}
