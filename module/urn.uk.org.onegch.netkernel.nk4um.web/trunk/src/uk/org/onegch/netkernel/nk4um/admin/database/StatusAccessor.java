package uk.org.onegch.netkernel.nk4um.admin.database;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class StatusAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/admin/database/");
    
    if (aContext.source("nk4um:db:liquibase:updateAvailable", Boolean.class)) {
      util.issueSourceRequestAsResponse("update.xml");
    } else {
      util.issueSourceRequestAsResponse("noUpdate.xml");
    }
  }
}
