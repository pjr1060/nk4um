package uk.org.onegch.netkernel.nk4um.admin.database;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFRequestReadOnly;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class CheckDatabaseStatusAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    INKFRequestReadOnly request= aContext.source("arg:request", INKFRequestReadOnly.class);
    
    if (request.getResolvedElementId().startsWith("nk4um:admin:database") ||
        request.getResolvedElementId().startsWith("nk4um:admin:configuration") ||
        request.getResolvedElementId().equals("nk4um:admin:buttonBar")) {
      INKFRequest innerRequest = request.getIssuableClone();
      aContext.createResponseFrom(innerRequest);
    } else if (aContext.source("nk4um:db:liquibase:updateAvailable", Boolean.class)) {
      aContext.sink("httpResponse:/redirect", "/nk4um/database/status");
    } else {
      INKFRequest innerRequest = request.getIssuableClone();
      aContext.createResponseFrom(innerRequest);
    }
  }
}
