package uk.org.onegch.netkernel.nk4um.web.user.profile;

import net.sf.saxon.s9api.XdmNode;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

import org.netkernel.layer0.representation.impl.HDSBuilder;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class ProfileFormAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/user/profile/");
    
    IHDSNode params;
    
    if (aContext.exists("session:/formData/name") &&
        aContext.source("session:/formData/name", String.class).equals("profile")) {
      params= aContext.source("session:/formData/params", IHDSNode.class);
      aContext.delete("session:/formData/name");
      aContext.delete("session:/formData/params");
    } else {
      params= util.issueSourceRequest("nk4um:db:user",
                                      IHDSNode.class,
                                      new Arg("id", "nk4um:security:currentUser"));
      HDSBuilder paramsBuilder= new HDSBuilder();
      paramsBuilder.pushNode("root");
      for (IHDSNode paramNode : params.getNodes("//row/*")) {
        if (paramNode.getName().equals("display_name") && params.getFirstValue("//display_name") == null) {
          paramsBuilder.addNode(paramNode.getName(), params.getFirstValue("//email"));
        } else {
          paramsBuilder.addNode(paramNode.getName(), paramNode.getValue());
        }
      }

      params= paramsBuilder.getRoot();
    }


    IHDSNode pdsState = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);

    XdmNode processedNode= util.issueSourceRequest("active:xslt2",
                                                   XdmNode.class,
                                                   new Arg("operator", "profile.xsl"),
                                                   new Arg("operand", "profile.xml"),
                                                   new ArgByValue("externalModel", (pdsState.getFirstValue("//security_external") != null)));

    XdmNode formNode= util.issueSourceRequest("active:xslt2",
                                              XdmNode.class,
                                              new Arg("operator", "../../common/form-template.xsl"),
                                              new ArgByValue("operand", processedNode),
                                              new ArgByValue("params", params));
    
    util.issueSourceRequestAsResponse("active:xrl2", 
                                      new ArgByValue("template", formNode));
  }
}
