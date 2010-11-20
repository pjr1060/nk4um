package uk.org.onegch.netkernel.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ActivateAccessor extends DatabaseAccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT     nk4um_user_account.id\n" +
                "FROM       nk4um_user_account\n" +
                "INNER JOIN nk4um_user_activation ON nk4um_user_activation.user_id=nk4um_user_account.id\n" +
                "WHERE      nk4um_user_account.email=?\n" +
                "AND        nk4um_user_activation.activation_code=?";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                         Boolean.class,
                                                         new ArgByValue("operand", sql),
                                                         new Arg("param", "arg:email"),
                                                         new Arg("param", "arg:activationCode"));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
  
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String updateSql= "UPDATE nk4um_user_account\n" +
                      "SET    activated='t'\n" +
                      "WHERE  email=?";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", updateSql),
                            new Arg("param", "arg:email"));
    String deleteSql= "DELETE \n" +
                      "FROM   nk4um_user_activation\n" +
                      "WHERE  user_id=(SELECT id\n" +
                      "                FROM   nk4um_user_account" +
                      "                WHERE  email=?)";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", deleteSql),
                            new Arg("param", "arg:email"));
    
    util.cutGoldenThread("nk4um:user");
  }
}
