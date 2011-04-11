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
