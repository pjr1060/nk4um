package uk.org.onegch.netkernel.nk4um.web.exception;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.representation.WrappedThrowable;

import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Arg;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class ExceptionHandlerAccessor extends Layer2AccessorImpl {
  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    Throwable ex= (Throwable)aContext.source("arg:exception", WrappedThrowable.class).getThrowable();
    aContext.sink("session:/message/class", "error");
    aContext.sink("session:/message/title", "Error");
    aContext.sink("session:/message/content", "There was an error encountered generating this page. Please try again, or visit the main forum page.");
    
    util.issueSourceRequestAsResponse("active:java",
                                      new Arg("class", "uk.org.onegch.netkernel.nk4um.web.style.StyleAccessor"),
                                      new Arg("operand", "res:/uk/org/onegch/netkernel/nk4um/web/exception/error.xml"));
  }
}
