package uk.org.onegch.netkernel.nk4um.db.liquibase;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class UpdateAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
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
    
    util.issueSourceRequestAsResponse("active:liquibase-update",
                                      new ArgByValue("context", "builtin-user"),
                                      new ArgByValue("parameters", params.getRoot()));
  }
}
