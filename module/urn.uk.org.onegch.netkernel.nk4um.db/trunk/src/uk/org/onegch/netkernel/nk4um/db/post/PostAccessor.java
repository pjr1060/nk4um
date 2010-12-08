package uk.org.onegch.netkernel.nk4um.db.post;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class PostAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "SELECT   id,\n" +
                "         forum_topic_id,\n" +
                "         author_id,\n" +
                "         posted_date,\n" +
                "         title,\n" +
                "         content\n" +
                "FROM     nk4um_forum_topic_post\n" +
                "WHERE    id=?;";
    INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                                         IHDSNode.class,
                                                         new ArgByValue("operand", sql),
                                                         new ArgByValue("param", aContext.source("arg:id")));
    
    resp.setHeader("no-cache", null);
    util.attachGoldenThread("nk4um:all", "nk4um:topic", "nk4um:post");
  }
  
  @Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql= "INSERT INTO nk4um_forum_topic_post\n" +
                "(\n" +
                "    forum_topic_id\n," +
                "    author_id\n," +
                "    posted_date\n," +
                "    title\n," +
                "    content\n" +
                ") VALUES (\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    NOW(),\n" +
                "    ?,\n" +
                "    ?\n" +
                ");";
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", sql),
                            new Arg("param", "arg:topicId"),
                            new Arg("param", "arg:authorId"),
                            new Arg("param", "arg:title"),
                            new Arg("param", "arg:content"));
    
    util.cutGoldenThread("nk4um:post");
  }
}
