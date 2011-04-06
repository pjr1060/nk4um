package uk.org.onegch.netkernel.nk4um.db.liquibase;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.DatabaseAccessorImpl;
import uk.org.onegch.netkernel.layer2.DatabaseUtil;

public class ChangeUserModelAccessor extends DatabaseAccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, DatabaseUtil util) throws Exception {
    HDSBuilder params= new HDSBuilder();
    params.pushNode("parameters");

    IHDSNode pdsState;

    try {
      pdsState= aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);
    } catch (Exception e) {
      throw new Exception("nk4um not initialized", e);
    }

    String context;
    if (pdsState.getFirstValue("//security_external") == null) {
      context= "builtin-user";

      params.pushNode("parameter");
      params.addNode("name", "user.table");
      params.addNode("value", "nk4um_user_account");
      params.popNode();
      params.pushNode("parameter");
      params.addNode("name", "user.id");
      params.addNode("value", "id");
      params.popNode();
    } else {
      context= "external-user";

      params.pushNode("parameter");
      params.addNode("name", "user.table");
      params.addNode("value", pdsState.getFirstValue("//security_userTable"));
      params.popNode();
      params.pushNode("parameter");
      params.addNode("name", "user.id");
      params.addNode("value", pdsState.getFirstValue("//security_userTableId"));
      params.popNode();
    }

    util.issueSourceRequestAsResponse("active:liquibase-clear-checksums",
                                      new ArgByValue("changelog", "res:/uk/org/onegch/netkernel/nk4um/db/liquibase/nk4um-user-table-changer.xml"),
                                      new ArgByValue("parameters", params.getRoot()));
    
    util.issueSourceRequestAsResponse("active:liquibase-update",
                                      new ArgByValue("changelog", "res:/uk/org/onegch/netkernel/nk4um/db/liquibase/nk4um-user-table-changer.xml"),
                                      new ArgByValue("context", "main," + context),
                                      new ArgByValue("parameters", params.getRoot()));

    util.cutGoldenThread("nk4um:all");
  }
}
