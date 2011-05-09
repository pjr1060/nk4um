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
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseAccessorImpl;
import org.netkernelroc.mod.layer2.DatabaseUtil;

public class SummaryAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String sql = "SELECT (SELECT count(id)\n" +
                 "        FROM   users) AS user_count,\n" +
                 "       (SELECT count(id)\n" +
                 "        FROM   forumgroups) AS forum_group_count,\n" +
                 "       (SELECT count(id)\n" +
                 "        FROM   forums) AS forum_count,\n" +
                 "       (SELECT count(id)\n" +
                 "        FROM   topics) AS topic_count,\n" +
                 "       (SELECT count(id)\n" +
                 "        FROM   entries) AS post_count,\n" +
                 "       (SELECT sum(hits.hits)\n" +
                 "         FROM  hits,\n" +
                 "               topics\n" +
                 "         WHERE topics.id=hits.topicid) AS hit_count,\n" +
                 "       (SELECT count(forumsubscribers.userid)\n" +
                 "        FROM   forumsubscribers,\n" +
                 "               forums\n" +
                 "        WHERE forums.id=forumsubscribers.forumid) AS subscriber_count,\n" +
                 "       (SELECT count(moderators.userid)\n" +
                 "        FROM   moderators,\n" +
                 "               forums\n" +
                 "        WHERE forums.id=moderators.forumid) AS moderator_count;";

    IHDSNode config = aContext.source("arg:config", IHDSNode.class);

    HDSBuilder jdbcConfig = new HDSBuilder();
    jdbcConfig.pushNode("config");
    jdbcConfig.pushNode("rdbms");

    jdbcConfig.addNode("jdbcDriver", config.getFirstValue("//jdbcDriver"));
    jdbcConfig.addNode("jdbcConnection", config.getFirstValue("//jdbcConnection"));
    jdbcConfig.addNode("user", config.getFirstValue("//jdbcUser"));
    jdbcConfig.addNode("password", config.getFirstValue("//jdbcPassword"));
    jdbcConfig.addNode("poolSize", "8");

    util.issueSourceRequestAsResponse("active:sqlPSQuery",
                                      new ArgByValue("operand", sql),
                                      new ArgByValue("configuration", jdbcConfig.getRoot()));
  }
}
