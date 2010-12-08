package uk.org.onegch.netkernel.liquibase;

import liquibase.Liquibase;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

public class UpdateAccessor extends AbstractLiquibaseAccessor {
  
  @Override
  public void onSource(INKFRequestContext aContext) throws Exception {
    Liquibase liquibase= createLiquibase(aContext);
    
    if (aContext.exists("arg:parameters")) {
      IHDSNode parameters= aContext.source("arg:parameters", IHDSNode.class);
      for (IHDSNode parameter : parameters.getNodes("//parameter")) {
        liquibase.setChangeLogParameter((String)parameter.getFirstValue("name"),
                                        (String)parameter.getFirstValue("value"));
      }
    }
    
    String liquibaseContext= null;
    if (aContext.exists("arg:context")) {
      liquibaseContext= aContext.source("arg:context", String.class);
    }
    
    liquibase.update(liquibaseContext);
    
    aContext.createResponseFrom(true);
  }
}
