package uk.org.onegch.netkernel.nk4um.admin.configuration;

import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class DetailsAccessor extends Layer2AccessorImpl
{
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/admin/configuration/");
    /*
    INKFRequest forumGroupReq= util.createSourceRequest("nk4um:db:forum",
                                                        IHDSNode.class,
                                                        new Arg("id", "arg:id"));
    */
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new Arg("operator", "editStyle.xsl"),
                                      new Arg("operand", "pds:/nk4um/config.xml"));
  }
}
