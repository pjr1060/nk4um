package uk.org.onegch.netkernel.nk4um.web.security;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFRequestReadOnly;
import org.netkernel.layer0.nkf.NKFException;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class AccessControlAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    INKFRequestReadOnly request= aContext.source("arg:request", INKFRequestReadOnly.class);
    String name= request.getResolvedElementId();
    
    System.out.println(aContext.source("nk4um:db:liquibase:update", String.class));
    
    INKFRequest processIncludesReq= util.createSourceRequest("active:xrl2",
                                                             null,
                                                             new Arg("template", "res:/uk/org/onegch/netkernel/nk4um/web/mapperConfig.xml"));
    
    String requiredRole= util.issueSourceRequest("active:xpath2",
                                                 String.class,
                                                 new ArgByRequest("operand", processIncludesReq),
                                                 new ArgByValue("operator", "xs:string(//endpoint[id='" + name +"']/role)"));
    
    if (requiredRole == null) {
      allowRequest(request, aContext);
    } else if (requiredRole.equalsIgnoreCase("User") && aContext.exists("session:/currentUser")) {
      allowRequest(request, aContext);
    } else {
      denyRequest(aContext);
    }
  }
  
  private void allowRequest(INKFRequestReadOnly request, INKFRequestContext aContext) throws NKFException {
    INKFRequest innerRequest = request.getIssuableClone();
    aContext.createResponseFrom(innerRequest);
  }
  
  private void denyRequest(INKFRequestContext aContext) throws NKFException {
    String url;
    if (!aContext.source("httpRequest:/url", String.class).endsWith("login")) {
      url= aContext.source("httpRequest:/url", String.class);
    } else {
      url= "/nk4um/";
    }
    
    aContext.sink("session:/message/class", "info");
    aContext.sink("session:/message/title", "Login Required");
    aContext.sink("session:/message/content", "You need to login to access this page.");
    
    aContext.sink("session:/loginRedirect", url);
    
    aContext.sink("httpResponse:/redirect", "/nk4um/user/login");
  }
}
