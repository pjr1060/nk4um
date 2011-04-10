package uk.org.onegch.netkernel.nk4um.db.topic;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

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
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:topic");
  }
  
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT     nk4um_forum_topic.id,\n" +
                "           nk4um_forum_group.title AS forum_group,\n" +
                "           nk4um_forum_topic.status,\n" +
                "          (     nk4um_topic_status.visible\n" +
                "            AND (SELECT count(id)\n" +
                "                 FROM   nk4um_forum_topic_post\n" +
                "                 WHERE  (SELECT  visible\n" +
                "                         FROM    nk4um_post_status\n" +
                "                         WHERE   nk4um_post_status.status=nk4um_forum_topic_post.status\n)" +
                "                 AND     nk4um_forum_topic_post.forum_topic_id=nk4um_forum_topic.id)>0) AS visible,\n" +
                "           nk4um_topic_status.display_order AS status_order,\n" +
                "           nk4um_forum.title AS forum,\n" +
                "           nk4um_forum_topic.forum_id,\n" +
                "           nk4um_forum_topic.author_id,\n" +
                "           nk4um_forum_topic.posted_date,\n" +
                "           nk4um_forum_topic.title\n" +
                "FROM       nk4um_forum_topic\n" +
                "INNER JOIN nk4um_forum        ON nk4um_forum.id=nk4um_forum_topic.forum_id\n" +
                "INNER JOIN nk4um_forum_group  ON nk4um_forum_group.id=nk4um_forum.forum_group_id\n" +
                "INNER JOIN nk4um_topic_status ON nk4um_topic_status.status=nk4um_forum_topic.status\n" +
                "WHERE      nk4um_forum_topic.id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
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
                            new ArgByValue("param", aContext.source("arg:forumId")),
                            new ArgByValue("param", aContext.source("arg:authorId")),
                            new ArgByValue("param", aContext.source("arg:title")));
    
    String insertPostSql= "INSERT INTO nk4um_forum_topic_post\n" +
                          "(\n" +
                          "    forum_topic_id,\n" +
                          "    author_id,\n" +
                          "    posted_date,\n" +
                          "    title,\n" +
                          "    content,\n" +
                          "    status\n" +
                          ") VALUES (\n" +
                          "    ?,\n" +
                          "    ?,\n" +
                          "    (SELECT posted_date\n" +
                          "     FROM   nk4um_forum_topic\n" +
                          "     WHERE  id=?),\n" +
                          "    ?,\n" +
                          "    ?,\n" +
                          "    ?\n" +
                          ");";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", insertPostSql),
                            new ArgByValue("param", nextId),
                            new ArgByValue("param", aContext.source("arg:authorId")),
                            new ArgByValue("param", nextId),
                            new ArgByValue("param", aContext.source("arg:title")),
                            new ArgByValue("param", aContext.source("arg:content")),
                            new ArgByValue("param", aContext.source("arg:postStatus")));
    
    util.cutGoldenThread("nk4um:post", "nk4um:topic");
    
    aContext.createResponseFrom(nextId);
  }
}
