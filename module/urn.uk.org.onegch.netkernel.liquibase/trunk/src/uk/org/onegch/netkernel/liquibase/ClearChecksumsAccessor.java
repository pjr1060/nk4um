package uk.org.onegch.netkernel.liquibase;

import liquibase.Liquibase;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

public class ClearChecksumsAccessor extends AbstractLiquibaseAccessor {
  
  @Override
  public void onSource(INKFRequestContext aContext) throws Exception {
    Liquibase liquibase= createLiquibase(aContext, aContext.source("arg:changelog", String.class));
    
    if (aContext.exists("arg:parameters")) {
      IHDSNode parameters= aContext.source("arg:parameters", IHDSNode.class);
      for (IHDSNode parameter : parameters.getNodes("//parameter")) {
        liquibase.setChangeLogParameter((String)parameter.getFirstValue("name"),
                                        (String)parameter.getFirstValue("value"));
      }
    }
    
    liquibase.clearCheckSums();
  }
}
