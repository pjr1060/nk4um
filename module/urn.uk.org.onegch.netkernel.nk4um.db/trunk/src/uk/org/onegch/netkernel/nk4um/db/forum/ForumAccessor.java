package uk.org.onegch.netkernel.nk4um.db.forum;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ForumAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT   id,\n" +
                "         display_order,\n" +
                "         forum_group_id,\n" +
                "         title,\n" +
                "         description\n" +
                "FROM     nk4um_forum\n" +
                "WHERE    id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new Arg("param", "arg:id"));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:forum");
  }
  
  @Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String nextIdSql= "SELECT nextval('nk4um_forum_id_seq') AS id;";
    IHDSNode nextIdNode= util.issueSourceRequest("active:sqlPSQuery",
                                                 IHDSNode.class,
                                                 new ArgByValue("operand", nextIdSql));
    Long nextId= (Long)nextIdNode.getFirstValue("//id");
    
    String insertForumGroupSql= "INSERT INTO nk4um_forum\n" +
                                "(\n" +
                                "    id,\n" +
                                "    forum_group_id,\n" +
                                "    display_order\n," +
                                "    title\n," +
                                "    description\n" +
                                ") VALUES (\n" +
                                "    ?,\n" +
                                "    ?,\n" +
                                "    ?,\n" +
                                "    ?,\n" +
                                "    ?\n" +
                                ");";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", insertForumGroupSql),
                            new ArgByValue("param", nextId),
                            new Arg("param", "arg:forumGroupId"),
                            new Arg("param", "arg:order"),
                            new Arg("param", "arg:title"),
                            new Arg("param", "arg:description"));
    
    util.cutGoldenThread("nk4um:forum");
    
    aContext.createResponseFrom(nextId);
  }

  @Override
  public void onSink(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String insertForumGroupSql= "UPDATE nk4um_forum\n" +
                                "SET    display_order=?,\n" +
                                "       title=?,\n" +
                                "       description=?\n" +
                                "WHERE  id=?;";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", insertForumGroupSql),
                            new Arg("param", "arg:order"),
                            new Arg("param", "arg:title"),
                            new Arg("param", "arg:description"),
                            new Arg("param", "arg:id"));
    
    util.cutGoldenThread("nk4um:forum");
  }
}
