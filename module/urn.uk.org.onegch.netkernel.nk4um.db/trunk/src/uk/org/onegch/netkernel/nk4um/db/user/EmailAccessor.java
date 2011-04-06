package uk.org.onegch.netkernel.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class EmailAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util)
      throws Exception {
    
    String email= util.escapeString(aContext.source("arg:email", String.class));
    
    String sql= "SELECT id\n" +
                "FROM   nk4um_user\n" +
                "WHERE  lower(email)=lower('" + email + "');";
    
    Long id= (Long)util.issueSourceRequest("active:sqlQuery",
                                           IHDSNode.class,
                                           new ArgByValue("operand", sql)).getFirstValue("//id");
    INKFResponse resp= aContext.createResponseFrom(id);
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util)
      throws Exception {

    String email= util.escapeString(aContext.source("arg:email", String.class));
    
    String sql= "SELECT id\n" +
                "FROM   nk4um_user\n" +
                "WHERE  lower(email)=lower('" + email + "');";
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlBooleanQuery",
                                                         new ArgByValue("operand", sql));

    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
}
