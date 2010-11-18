package uk.org.onegch.netkernel.nk4um.web.message;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class MessageAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/message/");
    
    if (aContext.exists("session:/message/title") &&
        aContext.exists("session:/message/content") &&
        aContext.exists("session:/message/class")) {
      
      util.issueSourceRequestAsResponse("active:xslt2",
                                        new Arg("operator", "message.xsl"),
                                        new Arg("operand", "message.xml"),
                                        new Arg("title", "session:/message/title"),
                                        new Arg("content", "session:/message/content"),
                                        new Arg("class", "session:/message/class"));
      
      aContext.delete("session:/message/class");
      aContext.delete("session:/message/title");
      aContext.delete("session:/message/content");
    } else {
      util.issueSourceRequestAsResponse("emptyMessage.xml");
    }
  }
}
