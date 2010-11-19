package uk.org.onegch.netkernel.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class UserAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT   id,\n" +
                "         username,\n" +
                "         email,\n" +
                "         display_name\n" +
                "FROM     nk4um_user\n" +
                "WHERE    id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new Arg("param", "arg:id"));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:user");
  }
  
  @Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    IHDSNode details= aContext.sourcePrimary(IHDSNode.class);
    
    String encryptedPassword= util.issueSourceRequest("active:sha512",
                                                      String.class,
                                                      new ArgByValue("operand", details.getFirstValue("//password")));
    
    String nextIdSql= "SELECT nextval('nk4um_forum_topic_id_seq') AS id;";
    IHDSNode nextIdNode= util.issueSourceRequest("active:sqlPSQuery",
                                                 IHDSNode.class,
                                                 new ArgByValue("operand", nextIdSql));
    Long nextId= (Long)nextIdNode.getFirstValue("//id");
    
    String newUserSql= "INSERT INTO nk4um_user_account\n" +
                       "(\n" +
                       "    id,\n" +
                       "    username,\n" +
                       "    password,\n" +
                       "    email\n" +
                       ") VALUES (\n" +
                       "    ?,\n" +
                       "    ?,\n" +
                       "    ?,\n" +
                       "    ?\n" +
                       ");";
    
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", newUserSql),
                            new ArgByValue("param", nextId),
                            new ArgByValue("param", details.getFirstValue("//username")),
                            new ArgByValue("param", encryptedPassword),
                            new ArgByValue("param", details.getFirstValue("//email")));
    
    String newUserMetaSql= "INSERT INTO nk4um_user_meta\n" +
                           "(\n" +
                           "    user_account_id,\n" +
                           "    display_name\n" +
                           ") VALUES (\n" +
                           "    ?,\n" +
                           "    ?\n" +
                           ");";
    
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", newUserMetaSql),
                            new ArgByValue("param", nextId),
                            new ArgByValue("param", details.getFirstValue("//display")));
    
    util.cutGoldenThread("nk4um:user");
  }
}
