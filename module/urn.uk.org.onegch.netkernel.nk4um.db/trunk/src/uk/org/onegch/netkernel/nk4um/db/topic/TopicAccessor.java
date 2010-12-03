package uk.org.onegch.netkernel.nk4um.db.topic;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class TopicAccessor extends DatabaseAccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT   id\n" +
                "FROM     nk4um_forum_topic\n" +
                "WHERE    id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                         new ArgByValue("operand", sql),
                                                         new Arg("param", "arg:id"));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:topic");
  }
  
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT   id,\n" +
                "         forum_id,\n" +
                "         author_id,\n" +
                "         posted_date,\n" +
                "         title\n" +
                "FROM     nk4um_forum_topic\n" +
                "WHERE    id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new Arg("param", "arg:id"));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:topic");
  }
  
  @Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String nextIdSql= "SELECT nextval('nk4um_forum_topic_id_seq') AS id;";
    IHDSNode nextIdNode= util.issueSourceRequest("active:sqlPSQuery",
                                                 IHDSNode.class,
                                                 new ArgByValue("operand", nextIdSql));
    Long nextId= (Long)nextIdNode.getFirstValue("//id");
    
    String insertTopicSql= "INSERT INTO nk4um_forum_topic\n" +
                           "(\n" +
                           "    id,\n" +
                           "    forum_id\n," +
                           "    author_id\n," +
                           "    posted_date\n," +
                           "    title\n" +
                           ") VALUES (\n" +
                           "    ?,\n" +
                           "    ?,\n" +
                           "    ?,\n" +
                           "    NOW(),\n" +
                           "    ?\n" +
                           ");";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", insertTopicSql),
                            new ArgByValue("param", nextId),
                            new Arg("param", "arg:forumId"),
                            new Arg("param", "arg:authorId"),
                            new Arg("param", "arg:title"));
    
    String insertPostSql= "INSERT INTO nk4um_forum_topic_post\n" +
                          "(\n" +
                          "    forum_topic_id\n," +
                          "    author_id\n," +
                          "    posted_date\n," +
                          "    title\n," +
                          "    content\n" +
                          ") VALUES (\n" +
                          "    ?,\n" +
                          "    ?,\n" +
                          "    (SELECT posted_date\n" +
                          "     FROM   nk4um_forum_topic\n" +
                          "     WHERE  id=?),\n" +
                          "    ?,\n" +
                          "    ?\n" +
                          ");";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", insertPostSql),
                            new ArgByValue("param", nextId),
                            new Arg("param", "arg:authorId"),
                            new ArgByValue("param", nextId),
                            new Arg("param", "arg:title"),
                            new Arg("param", "arg:content"));
    
    util.cutGoldenThread("nk4um:post", "nk4um:topic");
  }
}
