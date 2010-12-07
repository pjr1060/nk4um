package uk.org.onegch.netkernel.nk4um.web;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class SmtpConfigAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    IHDSNode pdsState;
    
    try {
      pdsState= aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);
    } catch (Exception e) {
      throw new Exception("nk4um not initialized", e);
    }
      
    HDSBuilder b = new HDSBuilder();
    
    
    if (pdsState.getFirstValue("//smtpGateway") != null &&
        pdsState.getFirstValue("//smtpSender") != null) {
      b.pushNode("SMTPConfig");
      b.addNode("gateway", pdsState.getFirstValue("//smtpGateway"));
      if (pdsState.getFirstValue("//smtpPort") != null &&
          !pdsState.getFirstValue("//smtpPort").equals("")) {
        b.addNode("port", pdsState.getFirstValue("//smtpPort"));
      }
      if (pdsState.getFirstValue("//smtpUser") != null &&
          !pdsState.getFirstValue("//smtpUser").equals("")) {
        b.addNode("user", pdsState.getFirstValue("//smtpUser"));
      }
      if (pdsState.getFirstValue("//smtpPassword") != null &&
          !pdsState.getFirstValue("//smtpPassword").equals("")) {
        b.addNode("password", pdsState.getFirstValue("//smtpPassword"));
      }
      
      if (pdsState.getFirstValue("//smtpSecurity").equals("tls")) {
        b.addNode("tls", "true");
      } else if (pdsState.getFirstValue("//smtpSecurity").equals("ssl")) {
        b.addNode("ssl", "true");
      }
      
      b.addNode("sender", pdsState.getFirstValue("//smtpSender"));
    } else {
      throw new Exception("nk4um not initialized");
    }
    
    aContext.createResponseFrom(b.getRoot());
  }
}
