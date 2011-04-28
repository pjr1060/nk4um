/*
 * Copyright (C) 2010-2011 by Chris Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.org.onegch.netkernel.nk4um.db.importer;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.NKFException;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ImportAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    Boolean importUser = aContext.source("arg:importUser", Boolean.class);

    IHDSNode config = aContext.source("arg:config", IHDSNode.class);

    HDSBuilder jdbcConfig = new HDSBuilder();
    jdbcConfig.pushNode("config");
    jdbcConfig.pushNode("rdbms");

    jdbcConfig.addNode("jdbcDriver", config.getFirstValue("//jdbcDriver"));
    jdbcConfig.addNode("jdbcConnection", config.getFirstValue("//jdbcConnection"));
    jdbcConfig.addNode("user", config.getFirstValue("//jdbcUser"));
    jdbcConfig.addNode("password", config.getFirstValue("//jdbcPassword"));
    jdbcConfig.addNode("poolSize", "8");

    config = jdbcConfig.getRoot();

    importUserTables(config, importUser, util);
    importForumGroupTables(config, util);
    importForumTables(config, util);
    importTopicTables(config, util);
    importPostTables(config, util);
    importViewTables(config, util);
    importModeratorTables(config, util);
    importSubscriptionTables(config, util);
  }

  private void importUserTables(IHDSNode config, Boolean importUser, DatabaseUtil util) throws NKFException {
    String sql = "SELECT * FROM users;";
    IHDSNode userNodes = util.issueSourceRequest("active:sqlPSQuery",
                                                 IHDSNode.class,
                                                 new ArgByValue("operand", sql),
                                                 new ArgByValue("configuration", config));

    for (IHDSNode userNode : userNodes.getNodes("//row")) {
      if (importUser) {
        String insertSql = "INSERT INTO nk4um_user_account\n" +
                           "(\n" +
                           "    id,\n" +
                           "    username,\n" +
                           "    password,\n" +
                           "    email,\n" +
                           "    activated\n" +
                           ") VALUES (\n" +
                           "    ?,\n" +
                           "    ?,\n" +
                           "    ?,\n" +
                           "    ?,\n" +
                           "    ?\n" +
                           ");";
        util.issueSourceRequest("active:sqlPSUpdate",
                                null,
                                new ArgByValue("operand", insertSql),
                                new ArgByValue("param", userNode.getFirstValue("id")),
                                new ArgByValue("param", userNode.getFirstValue("email")),
                                new ArgByValue("param", userNode.getFirstValue("hashedpwd")),
                                new ArgByValue("param", userNode.getFirstValue("email")),
                                new ArgByValue("param", userNode.getFirstValue("status").equals("active")));
      }
      String insertSql = "INSERT INTO nk4um_user_meta\n" +
                         "(\n" +
                         "    user_account_id,\n" +
                         "    display_name\n" +
                         ") VALUES (\n" +
                         "    ?,\n" +
                         "    ?\n" +
                         ");";
      util.issueSourceRequest("active:sqlPSUpdate",
                              null,
                              new ArgByValue("operand", insertSql),
                              new ArgByValue("param", userNode.getFirstValue("id")),
                              new ArgByValue("param", userNode.getFirstValue("userid")));
    }
  }
  
  private void importForumGroupTables(IHDSNode config, DatabaseUtil util) throws NKFException {
    String sql = "SELECT * FROM forumgroups;";
    IHDSNode forumGroupsNodes = util.issueSourceRequest("active:sqlPSQuery",
                                                        IHDSNode.class,
                                                        new ArgByValue("operand", sql),
                                                        new ArgByValue("configuration", config));

    for (IHDSNode forumGroupNode : forumGroupsNodes.getNodes("//row")) {
      String description = (String) forumGroupNode.getFirstValue("descr");
      if (description == null || description.trim().equals("")) {
        description = (String) forumGroupNode.getFirstValue("name");
      }

      String insertSql = "INSERT INTO nk4um_forum_group\n" +
                         "(\n" +
                         "    id,\n" +
                         "    title,\n" +
                         "    description,\n" +
                         "    display_order\n" +
                         ") VALUES (\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?\n" +
                         ");";
      util.issueSourceRequest("active:sqlPSUpdate",
                              null,
                              new ArgByValue("operand", insertSql),
                              new ArgByValue("param", forumGroupNode.getFirstValue("id")),
                              new ArgByValue("param", forumGroupNode.getFirstValue("name")),
                              new ArgByValue("param", description),
                              new ArgByValue("param", ((Long)forumGroupNode.getFirstValue("position")).intValue()));
    }
  }

  private void importForumTables(IHDSNode config, DatabaseUtil util) throws NKFException {
    String sql = "SELECT * FROM forums;";
    IHDSNode forumNodes = util.issueSourceRequest("active:sqlPSQuery",
                                                  IHDSNode.class,
                                                  new ArgByValue("operand", sql),
                                                  new ArgByValue("configuration", config));

    for (IHDSNode forumNode : forumNodes.getNodes("//row")) {
      String description = (String) forumNode.getFirstValue("descr");
      if (description == null || description.trim().equals("")) {
        description = (String) forumNode.getFirstValue("name");
      }

      String insertSql = "INSERT INTO nk4um_forum\n" +
                         "(\n" +
                         "    id,\n" +
                         "    forum_group_id,\n" +
                         "    title,\n" +
                         "    description,\n" +
                         "    display_order\n" +
                         ") VALUES (\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?\n" +
                         ");";
      util.issueSourceRequest("active:sqlPSUpdate",
                              null,
                              new ArgByValue("operand", insertSql),
                              new ArgByValue("param", forumNode.getFirstValue("id")),
                              new ArgByValue("param", forumNode.getFirstValue("groupid")),
                              new ArgByValue("param", forumNode.getFirstValue("name")),
                              new ArgByValue("param", description),
                              new ArgByValue("param", ((Long)forumNode.getFirstValue("position")).intValue()));
    }
  }

  private void importTopicTables(IHDSNode config, DatabaseUtil util) throws NKFException {
    String sql = "SELECT * FROM topics;";
    IHDSNode topicNodes = util.issueSourceRequest("active:sqlPSQuery",
                                                  IHDSNode.class,
                                                  new ArgByValue("operand", sql),
                                                  new ArgByValue("configuration", config));

    for (IHDSNode topicNode : topicNodes.getNodes("//row")) {
      String status;

      String origStatus = (String) topicNode.getFirstValue("status");
      String origFlag = (String) topicNode.getFirstValue("flag");

      if (origStatus.equals("removed")) {
        status = "deleted";
      } else if (origFlag.equals("normal")) {
        status = "active";
      } else if (origFlag.equals("sticky") || origFlag.equals("announce")) {
        status = "sticky";
      } else {
        throw new NKFException("Unsupported status/flag mix of '" + origStatus + "' and '" + origFlag + "'");
      }

      String insertSql = "INSERT INTO nk4um_forum_topic\n" +
                         "(\n" +
                         "    id,\n" +
                         "    forum_id,\n" +
                         "    author_id,\n" +
                         "    posted_date,\n" +
                         "    title,\n" +
                         "    status\n" +
                         ") VALUES (\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?\n" +
                         ");";
      util.issueSourceRequest("active:sqlPSUpdate",
                              null,
                              new ArgByValue("operand", insertSql),
                              new ArgByValue("param", topicNode.getFirstValue("id")),
                              new ArgByValue("param", topicNode.getFirstValue("forumid")),
                              new ArgByValue("param", topicNode.getFirstValue("author")),
                              new ArgByValue("param", topicNode.getFirstValue("created")),
                              new ArgByValue("param", topicNode.getFirstValue("title")),
                              new ArgByValue("param", status));
    }
  }

  private void importPostTables(IHDSNode config, DatabaseUtil util) throws NKFException {
    String sql = "SELECT * FROM entries;";
    IHDSNode postNodes = util.issueSourceRequest("active:sqlPSQuery",
                                                 IHDSNode.class,
                                                 new ArgByValue("operand", sql),
                                                 new ArgByValue("configuration", config));

    for (IHDSNode postNode : postNodes.getNodes("//row")) {
      String insertSql = "INSERT INTO nk4um_forum_topic_post\n" +
                         "(\n" +
                         "    id,\n" +
                         "    forum_topic_id,\n" +
                         "    author_id,\n" +
                         "    posted_date,\n" +
                         "    title,\n" +
                         "    content\n" +
                         ") VALUES (\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    ?\n" +
                         ");";
      util.issueSourceRequest("active:sqlPSUpdate",
                              null,
                              new ArgByValue("operand", insertSql),
                              new ArgByValue("param", postNode.getFirstValue("id")),
                              new ArgByValue("param", postNode.getFirstValue("topicid")),
                              new ArgByValue("param", postNode.getFirstValue("author")),
                              new ArgByValue("param", postNode.getFirstValue("created")),
                              new ArgByValue("param", postNode.getFirstValue("title")),
                              new ArgByValue("param", postNode.getFirstValue("entry")));
    }
  }

  private void importViewTables(IHDSNode config, DatabaseUtil util) throws NKFException {
    String sql = "SELECT hits.* FROM hits, topics WHERE topics.id=hits.topicid ORDER BY topicid;";
    IHDSNode viewNodes = util.issueSourceRequest("active:sqlPSQuery",
                                                 IHDSNode.class,
                                                 new ArgByValue("operand", sql),
                                                 new ArgByValue("configuration", config));

    for (IHDSNode viewNode : viewNodes.getNodes("//row")) {
      int hits = ((Long)viewNode.getFirstValue("hits")).intValue();

      for (int i = 0; i < hits; i++) {
        String insertSql = "INSERT INTO nk4um_topic_view\n" +
                           "(\n" +
                           "    topic_id\n" +
                           ") VALUES (\n" +
                           "    ?\n" +
                           ");";
        util.issueSourceRequest("active:sqlPSUpdate",
                                null,
                                new ArgByValue("operand", insertSql),
                                new ArgByValue("param", viewNode.getFirstValue("topicid")));
      }
    }
  }

  private void importModeratorTables(IHDSNode config, DatabaseUtil util) throws NKFException {
    String sql = "SELECT moderators.* FROM moderators, forums WHERE forums.id=moderators.forumid ORDER BY forumid;";
    IHDSNode moderatorNodes = util.issueSourceRequest("active:sqlPSQuery",
                                                      IHDSNode.class,
                                                      new ArgByValue("operand", sql),
                                                      new ArgByValue("configuration", config));

    for (IHDSNode moderatorNode : moderatorNodes.getNodes("//row")) {
      String insertSql = "INSERT INTO nk4um_forum_moderator\n" +
                         "(\n" +
                         "    forum_id,\n" +
                         "    user_id\n" +
                         ") VALUES (\n" +
                         "    ?,\n" +
                         "    ?\n" +
                         ");";
      util.issueSourceRequest("active:sqlPSUpdate",
                              null,
                              new ArgByValue("operand", insertSql),
                              new ArgByValue("param", moderatorNode.getFirstValue("forumid")),
                              new ArgByValue("param", moderatorNode.getFirstValue("userid")));
    }
  }

  private void importSubscriptionTables(IHDSNode config, DatabaseUtil util) throws NKFException {
    String sql = "SELECT forumsubscribers.* FROM forumsubscribers, forums WHERE forums.id=forumsubscribers.forumid ORDER BY forumid;";
    IHDSNode subscribersNodes = util.issueSourceRequest("active:sqlPSQuery",
                                                      IHDSNode.class,
                                                      new ArgByValue("operand", sql),
                                                      new ArgByValue("configuration", config));

    for (IHDSNode subscriberNode : subscribersNodes.getNodes("//row")) {
      String insertSql = "INSERT INTO nk4um_forum_subscription\n" +
                         "(\n" +
                         "    forum_id,\n" +
                         "    user_id,\n" +
                         "    notification_type_name\n" +
                         ") VALUES (\n" +
                         "    ?,\n" +
                         "    ?,\n" +
                         "    'email'\n" +
                         ");";
      util.issueSourceRequest("active:sqlPSUpdate",
                              null,
                              new ArgByValue("operand", insertSql),
                              new ArgByValue("param", subscriberNode.getFirstValue("forumid")),
                              new ArgByValue("param", subscriberNode.getFirstValue("userid")));
    }
  }
}
