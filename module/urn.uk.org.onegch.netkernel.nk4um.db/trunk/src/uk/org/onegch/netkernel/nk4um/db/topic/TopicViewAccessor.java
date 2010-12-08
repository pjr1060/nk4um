package uk.org.onegch.netkernel.nk4um.db.topic;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.nkf.NKFException;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class TopicViewAccessor extends DatabaseAccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    String userId= aContext.source("arg:userId", String.class);
    
    if (userId == null) {
      String sql= "SELECT id\n" +
                  "FROM   nk4um_topic_view\n" +
                  "WHERE  topic_id=?\n" +
                  "AND    ip_address_id=(SELECT id FROM nk4um_ip_address WHERE ip_address=?)\n" +
                  "AND    user_agent_id=(SELECT id FROM nk4um_user_agent WHERE user_agent=?)\n" +
                  "AND    view_date > NOW()-interval '1 hour';";
      INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                           Boolean.class,
                                                           new ArgByValue("operand", sql),
                                                           new ArgByValue("param", aContext.source("arg:id")),
                                                           new ArgByValue("param", aContext.source("arg:ipAddress")),
                                                           new ArgByValue("param", aContext.source("arg:userAgent")));
      
      resp.setExpiry(INKFResponse.EXPIRY_ALWAYS);
    } else {
      String sql= "SELECT id\n" +
                  "FROM   nk4um_topic_view\n" +
                  "WHERE  topic_id=?\n" +
                  "AND    user_id=?\n" +
                  "AND    view_date > NOW()-interval '1 hour';";
      INKFResponse resp= util.issueSourceRequestAsResponse("active:sqlPSBooleanQuery",
                                                           Boolean.class,
                                                           new ArgByValue("operand", sql),
                                                           new Arg("param", "arg:id"),
                                                           new Arg("param", "arg:userId"));
      
      resp.setExpiry(INKFResponse.EXPIRY_ALWAYS);
    }
  }
  
  @Override
  public void onNew(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    addIpAddressId(aContext, aContext.source("arg:ipAddress", String.class));
    addUserAgent(aContext, aContext.source("arg:userAgent", String.class));
    
    String sql= "INSERT INTO nk4um_topic_view\n" +
                "(\n" +
                "  topic_id,\n" +
                "  user_id,\n" +
                "  ip_address_id,\n" +
                "  user_agent_id,\n" +
                "  view_date\n" +
                ") VALUES (\n" +
                "  ?,\n" +
                "  ?,\n" +
                "  (SELECT id FROM nk4um_ip_address WHERE ip_address=?),\n" +
                "  (SELECT id FROM nk4um_user_agent WHERE user_agent=?),\n" +
                "  NOW()\n" +
                ")";
    
    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", sql),
                            new ArgByValue("param", aContext.source("arg:id")),
                            new ArgByValue("param", aContext.source("arg:userId")),
                            new ArgByValue("param", aContext.source("arg:ipAddress")),
                            new ArgByValue("param", aContext.source("arg:userAgent")));
    
    util.cutGoldenThread("nk4um:topic");
  }
  
  public static void addIpAddressId(INKFRequestContext aContext, String ipAddress) throws NKFException {
    String selectIpAddressSql= "SELECT id FROM nk4um_ip_address WHERE ip_address=?;";
    String insertIpAddressSql= "INSERT INTO nk4um_ip_address (ip_address) VALUES (?);";
    
    synchronized(TopicViewAccessor.class) {
      // add IP address if needed
      INKFRequest checkIpReq= aContext.createRequest("active:sqlPSBooleanQuery");
      checkIpReq.setRepresentationClass(Boolean.class);
      checkIpReq.addArgumentByValue("operand", selectIpAddressSql);
      checkIpReq.addArgumentByValue("param", ipAddress);
      if (!(Boolean)aContext.issueRequest(checkIpReq)) {
        INKFRequest addIpReq= aContext.createRequest("active:sqlPSUpdate");
        addIpReq.addArgumentByValue("operand", insertIpAddressSql);
        addIpReq.addArgumentByValue("param", ipAddress);
        aContext.issueRequest(addIpReq);
      }
    }
  }
  
  public static void addUserAgent(INKFRequestContext aContext, String userAgent) throws NKFException {
    String selectUserAgentSql= "SELECT id FROM nk4um_user_agent WHERE user_agent=?;";
    String insertUserAgentSql= "INSERT INTO nk4um_user_agent (user_agent) VALUES (?);";
    
    synchronized(TopicViewAccessor.class) {
      // add user-agent if needed
      INKFRequest checkUserAgentReq= aContext.createRequest("active:sqlPSBooleanQuery");
      checkUserAgentReq.setRepresentationClass(Boolean.class);
      checkUserAgentReq.addArgumentByValue("operand", selectUserAgentSql);
      checkUserAgentReq.addArgumentByValue("param", userAgent);
      if (!(Boolean)aContext.issueRequest(checkUserAgentReq)) {
        INKFRequest addUserAgentReq= aContext.createRequest("active:sqlPSUpdate");
        addUserAgentReq.addArgumentByValue("operand", insertUserAgentSql);
        addUserAgentReq.addArgumentByValue("param", userAgent);
        aContext.issueRequest(addUserAgentReq);
      }
    }
  }
}
