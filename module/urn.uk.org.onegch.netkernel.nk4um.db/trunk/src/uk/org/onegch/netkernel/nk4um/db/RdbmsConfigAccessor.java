package uk.org.onegch.netkernel.nk4um.db;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class RdbmsConfigAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    IHDSNode pdsState;
    
    try {
      pdsState= aContext.source("pds:/nk4um/config.xml", IHDSNode.class);
    } catch (Exception e) {
      throw new Exception("nk4um not initialized", e);
    }
      
    HDSBuilder b = new HDSBuilder();
    
    if (pdsState.getFirstValue("//jdbcDriver") != null ||
        pdsState.getFirstValue("//jdbcConnection") != null) {
      b.pushNode("config");
      b.pushNode("rdbms");
      b.addNode("jdbcDriver", pdsState.getFirstValue("//jdbcDriver"));
      b.addNode("jdbcConnection", pdsState.getFirstValue("//jdbcConnection"));
      b.addNode("user", pdsState.getFirstValue("//jdbcUser"));
      b.addNode("password", pdsState.getFirstValue("//jdbcPassword"));
      b.addNode("poolSize", "4");
    } else {
      throw new Exception("nk4um not initialized");
    }
    
    aContext.createResponseFrom(b.getRoot());
  }
}
