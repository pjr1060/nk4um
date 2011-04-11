package uk.org.onegch.netkernel.nk4um.web.topic.add;

import net.sf.saxon.s9api.XdmNode;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class AddFormAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/topic/add/");
    
    IHDSNode params;
    
    if (aContext.exists("session:/formData/name") &&
        aContext.source("session:/formData/name", String.class).equals("addTopic")) {
      params= aContext.source("session:/formData/params", IHDSNode.class);
      aContext.delete("session:/formData/name");
      aContext.delete("session:/formData/params");
    } else {
      HDSBuilder builder= new HDSBuilder();
      builder.pushNode("root");
      params= builder.getRoot();
    }
    
    XdmNode formNode= util.issueSourceRequest("active:xslt2",
                                              XdmNode.class,
                                              new Arg("operator", "../../common/form-template.xsl"),
                                              new Arg("operand", "add.xml"),
                                              new ArgByValue("params", params));
    
    util.issueSourceRequestAsResponse("active:xrl2", 
                                      new ArgByValue("template", formNode),
                                      new Arg("id", "arg:id"));
  }
}
