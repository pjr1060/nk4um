package uk.org.onegch.netkernel.nk4um.web.navigation;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class NavigationAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/navigation/");
    
    if (aContext.exists("session:/currentUser")) {
      util.issueSourceRequestAsResponse("active:xslt2",
                                        new Arg("operator", "loggedIn.xsl"),
                                        new Arg("operand", "loggedIn.xml"),
                                        new Arg("userId", "session:/currentUser"));
    } else {
      util.issueSourceRequestAsResponse("notLoggedIn.xml");
    }
  }
}
