package uk.org.onegch.netkernel.nk4um.web;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.ArgByRequest;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class MapperConfigAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    aContext.setCWU("res:/uk/org/onegch/netkernel/nk4um/web/");
    INKFRequest processIncludesReq= util.createSourceRequest("active:xrl2",
                                                             null,
                                                             new Arg("template", "mapperConfig.xml"));
    
    util.issueSourceRequestAsResponse("active:xslt2",
                                      new ArgByRequest("operand", processIncludesReq),
                                      new Arg("operator", "mapperConfig.xsl"));
  }
}
