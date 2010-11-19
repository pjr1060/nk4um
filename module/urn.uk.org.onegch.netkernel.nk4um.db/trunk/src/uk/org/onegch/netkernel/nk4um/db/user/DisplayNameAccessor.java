package uk.org.onegch.netkernel.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class DisplayNameAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util)
      throws Exception {
    
    String displayName= util.escapeString(aContext.source("arg:displayName", String.class));
    
    String sql= "SELECT user_account_id\n" +
                "FROM   nk4um_user_meta\n" +
                "WHERE  lower(display_name)=lower('" + displayName + "');";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlQuery",
                                                         new ArgByValue("operand", sql));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util)
      throws Exception {

    String displayName= util.escapeString(aContext.source("arg:displayName", String.class));
    
    String sql= "SELECT user_account_id\n" +
                "FROM   nk4um_user_meta\n" +
                "WHERE  lower(display_name)=lower('" + displayName + "');";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlBooleanQuery",
                                                         new ArgByValue("operand", sql));

    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
}
