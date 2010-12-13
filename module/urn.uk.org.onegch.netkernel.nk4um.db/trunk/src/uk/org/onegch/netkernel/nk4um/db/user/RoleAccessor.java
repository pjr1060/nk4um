package uk.org.onegch.netkernel.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class RoleAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "UPDATE nk4um_user_meta\n" +
                "SET    role_name=?\n" +
                "WHERE  user_account_id=?;";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSUpdate",
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.sourcePrimary(String.class)),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    resp.setHeader("no-cache", null);
    util.cutGoldenThread("nk4um:user");
  }
}
