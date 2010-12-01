package uk.org.onegch.netkernel.nk4um.web.user.profile;

import net.sf.saxon.s9api.XdmNode;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.IHDSNode;

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
                                      new Arg("id", "session:/currentUser"));
    }
    
    XdmNode formNode= util.issueSourceRequest("active:xslt2",
                                              XdmNode.class,
                                              new Arg("operator", "../../common/form-template.xsl"),
                                              new Arg("operand", "profile.xml"),
                                              new ArgByValue("params", params));
    
    util.issueSourceRequestAsResponse("active:xrl2", 
                                      new ArgByValue("template", formNode));
  }
}
