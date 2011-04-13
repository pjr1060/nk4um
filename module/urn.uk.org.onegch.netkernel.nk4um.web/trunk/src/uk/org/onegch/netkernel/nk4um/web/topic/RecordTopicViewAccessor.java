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

package uk.org.onegch.netkernel.nk4um.web.topic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFRequestReadOnly;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.nkf.NKFException;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class RecordTopicViewAccessor extends Layer2AccessorImpl {
  private static final Pattern pattern= Pattern.compile(".*/(\\d+)/(index)?");
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    INKFRequestReadOnly request= aContext.source("arg:request",INKFRequestReadOnly.class);
    
    Matcher matcher= pattern.matcher(request.getIdentifier());
    if (matcher.matches()) {
      String topicId= matcher.group(1);
      
      if (util.issueExistsRequest("nk4um:db:topic",
                                  new ArgByValue("id", topicId))) {
        String ipAddress= getIpAddress(aContext);
        String userAgent= getUserAgent(aContext);
        String currentUser= aContext.source("nk4um:security:currentUser", String.class);
        if (!util.issueExistsRequest("nk4um:db:topic:view",
                                     new ArgByValue("id", topicId),
                                     new ArgByValue("ipAddress", ipAddress),
                                     new ArgByValue("userAgent", userAgent),
                                     new ArgByValue("userId", currentUser))) {
          util.issueNewRequest("nk4um:db:topic:view",
                               null,
                               null,
                               new ArgByValue("id", topicId),
                               new ArgByValue("ipAddress", ipAddress),
                               new ArgByValue("userAgent", userAgent),
                               new ArgByValue("userId", currentUser));
        }
      }
    } else {
      throw new Exception("Malformed topic request URL. Reg-ex is probably broken. Regex: " +
                          pattern.pattern() + " and request URL: " + request.getIdentifier());
    }
    
    INKFRequest reqOut=request.getIssuableClone();
    INKFResponse resp= aContext.createResponseFrom(reqOut);
    resp.setExpiry(INKFResponse.EXPIRY_ALWAYS);
  }
  
  public static String getIpAddress(INKFRequestContext aContext) throws NKFException {
    String ipAddress= "unknown";
    
    if (aContext.exists("httpRequest:/header/X-Forwarded-For")) {
      ipAddress= aContext.source("httpRequest:/header/X-Forwarded-For", String.class);
    } else if (aContext.exists("httpRequest:/remote-host")) {
      ipAddress= aContext.source("httpRequest:/remote-host", String.class);
    }
    
    return ipAddress;
  }
    
  public static String getUserAgent(INKFRequestContext aContext) throws NKFException {
    String userAgent= "unknown";
    
    if (aContext.exists("httpRequest:/header/User-Agent")) {
      userAgent= aContext.source("httpRequest:/header/User-Agent", String.class);
    }
    
    return userAgent;
  }
}
