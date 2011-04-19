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

package uk.org.onegch.netkernel.nk4um.web.user.profile;

import java.util.ArrayList;
import java.util.List;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DoUpdateSubscriptionsAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    IHDSNode currentUserSubscriptions= util.issueSourceRequest("nk4um:db:forum:subscription:list",
                                                               IHDSNode.class,
                                                               new Arg("userId", "nk4um:security:currentUser"));
    
    List<Long> currentSubscriptionIds= new ArrayList<Long>();
    
    for (IHDSNode row : currentUserSubscriptions.getNodes("//row")) {
      currentSubscriptionIds.add((Long)row.getFirstValue("forum_id"));
    }
    
    IHDSNode paramsNode= aContext.source("httpRequest:/params",
                                         IHDSNode.class);
    
    for (IHDSNode param : paramsNode.getNodes("/*")) {
      String paramName= param.getName();
      if (paramName.startsWith("forum_")) {
        long forumId= Long.parseLong(paramName.substring(paramName.indexOf("forum_") + "forum_".length()));
        if (currentSubscriptionIds.contains(forumId)) {
          currentSubscriptionIds.remove(forumId);
        } else {
          util.issueNewRequest("nk4um:db:forum:subscription",
                               null,
                               null,
                               new ArgByValue("id", forumId),
                               new Arg("userId", "nk4um:security:currentUser"));
        }
      }
    }
    
    // any remaining subscriptions have been removed
    for (Long forumId : currentSubscriptionIds) {
      util.issueDeleteRequest("nk4um:db:forum:subscription",
                              new ArgByValue("id", forumId),
                              new Arg("userId", "nk4um:security:currentUser"));
    }
    
    aContext.sink("session:/message/class", "success");
    aContext.sink("session:/message/title", "Update Forum Subscriptions: success");
    aContext.sink("session:/message/content", "Your forum subscriptions have been updated");
    aContext.sink("httpResponse:/redirect", "profile");
  }
}
