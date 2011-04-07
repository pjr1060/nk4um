package uk.org.onegch.netkernel.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class RoleAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String existsSql = "SELECT user_account_id FROM nk4um_user_meta WHERE user_account_id=?";

    String updateMetaSql;
    INKFResponse resp;
    if (util.issueSourceRequest("active:sqlPSBooleanQuery",
                                Boolean.class,
                                new ArgByValue("operand", existsSql),
                                new ArgByValue("param", aContext.source("arg:id")))) {
      String sql= "UPDATE nk4um_user_meta\n" +
                  "SET    role_name=?\n" +
                  "WHERE  user_account_id=?;";

      resp= util.issueSourceRequestAsResponse("active:sqlPSUpdate",
                                              new ArgByValue("operand", sql),
                                              new ArgByValue("param", aContext.sourcePrimary(String.class)),
                                              new ArgByValue("param", aContext.source("arg:id")));
    } else {
      String sql= "INSERT INTO nk4um_user_meta (display_name, user_account_id, role_name)\n" +
                  "VALUES                      ((SELECT email FROM nk4um_user WHERE id=?), ?, ?)";

      resp= util.issueSourceRequestAsResponse("active:sqlPSUpdate",
                                              new ArgByValue("operand", sql),
                                              new ArgByValue("param", aContext.source("arg:id")),
                                              new ArgByValue("param", aContext.source("arg:id")),
                                              new ArgByValue("param", aContext.sourcePrimary(String.class)));
    }

    resp.setHeader("no-cache", null);
    util.cutGoldenThread("nk4um:user");
  }
}
