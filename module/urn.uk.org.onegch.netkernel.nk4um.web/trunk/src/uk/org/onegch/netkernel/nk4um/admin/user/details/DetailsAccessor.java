package uk.org.onegch.netkernel.nk4um.admin.user.details;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.representation.IHDSNode;
import org.w3c.dom.Document;

import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByValue;
import uk.org.onegch.netkernel.layer2.HttpLayer2AccessorImpl;
import uk.org.onegch.netkernel.layer2.HttpUtil;

public class DetailsAccessor extends HttpLayer2AccessorImpl {
  @Override
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/admin/user/details/");
    
    IHDSNode detailsNode= util.issueSourceRequest("nk4um:db:user", IHDSNode.class, new Arg("id", "arg:id"));
    
    INKFResponse resp= util.issueSourceRequestAsResponse("active:xslt2",
                                                         Document.class,
                                                         new Arg("operator", "detailsStyle.xsl"),
                                                         new ArgByValue("operand", detailsNode));

    String displayName;
    if (detailsNode.getFirstValue("//display_name") != null) {
      displayName = (String) detailsNode.getFirstValue("//display_name");
    } else {
      displayName = (String) detailsNode.getFirstValue("//email");
    }

    resp.setHeader("WrappedControlPanel", "User Details");
    resp.setHeader("WrappedControlPanelSubtitle", displayName);
  }
}
