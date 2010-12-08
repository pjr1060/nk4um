package uk.org.onegch.netkernel.nk4um.admin.database;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DoUpdateAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.source("nk4um:db:liquibase:update");
    
    aContext.sink("httpResponse:/redirect", "status");
  }
}
