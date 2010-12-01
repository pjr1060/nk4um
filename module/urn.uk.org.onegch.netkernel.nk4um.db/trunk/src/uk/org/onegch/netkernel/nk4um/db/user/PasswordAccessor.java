package uk.org.onegch.netkernel.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class PasswordAccessor extends DatabaseAccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String encryptedPassword= util.issueSourceRequest("active:sha512",
                                                      String.class,
                                                      new ArgByValue("operand", aContext.source("arg:password")));
    
    String sql= "SELECT id\n" +
                "FROM   nk4um_user_account\n" +
                "WHERE  id=?\n" +
                "AND    password=?";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                         Boolean.class,
                                                         new ArgByValue("operand", sql),
                                                         new Arg("param", "arg:id"),
                                                         new ArgByValue("param", encryptedPassword));
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
  
  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String encryptedPassword= util.issueSourceRequest("active:sha512",
                                                      String.class,
                                                      new ArgByValue("operand", aContext.source("arg:password")));
    
    String sql= "UPDATE nk4um_user_account\n" +
                "SET    password=?\n" +
                "WHERE  id=?;";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSUpdate",
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", encryptedPassword),
                                                         new Arg("param", "arg:id"));
    resp.setHeader("no-cache", null);
    util.cutGoldenThread("nk4um:user");
  }
}
