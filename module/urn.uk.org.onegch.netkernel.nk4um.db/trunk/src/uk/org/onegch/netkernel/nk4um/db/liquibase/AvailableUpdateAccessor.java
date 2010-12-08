package uk.org.onegch.netkernel.nk4um.db.liquibase;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class AvailableUpdateAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    HDSBuilder params= new HDSBuilder();
    params.pushNode("parameters");
    params.pushNode("parameter");
    params.addNode("name", "user.table");
    params.addNode("value", "nk4um_user_account");
    params.popNode();
    params.pushNode("parameter");
    params.addNode("name", "user.id");
    params.addNode("value", "id");
    params.popNode();
    
    util.issueSourceRequestAsResponse("active:liquibase-update-available",
                                      Boolean.class,
                                      new ArgByValue("context", "builtin-user"),
                                      new ArgByValue("parameters", params.getRoot()));
    
    util.attachGoldenThread("nk4um:all");
  }
}
