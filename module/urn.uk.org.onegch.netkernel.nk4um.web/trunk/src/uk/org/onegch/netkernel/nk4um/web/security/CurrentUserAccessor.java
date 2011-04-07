package uk.org.onegch.netkernel.nk4um.web.security;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.NKFException;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.urii.SimpleIdentifierImpl;
import org.netkernel.request.IRequestScopeLevel;
import org.netkernel.request.impl.RequestScopeLevelImpl;
import org.netkernel.urii.IIdentifier;
import org.netkernel.urii.ISpace;
import org.netkernel.urii.impl.Version;
import uk.org.onegch.netkernel.layer2.AccessorUtil;
import uk.org.onegch.netkernel.layer2.Layer2AccessorImpl;

public class CurrentUserAccessor extends Layer2AccessorImpl {
  @Override
  public void onExists(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    boolean loggedIn;
    IHDSNode pdsState;

    try {
      pdsState = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);

      if (pdsState.getFirstValue("//security_external") == null) {
        loggedIn = aContext.exists("session:/currentUser");
      } else {
        INKFRequest request = util.createExistsRequest((String) pdsState.getFirstValue("//security_userIdResource"));
        request.setRequestScope(buildRequestScope(aContext, (String) pdsState.getFirstValue("//security_gatekeeperModule")));
        
        loggedIn = (Boolean)aContext.issueRequest(request);
      }
      aContext.createResponseFrom(loggedIn);
    } catch (Exception e) {
      aContext.createResponseFrom(false);
    }
  }

  @Override
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    Object result;
    IHDSNode pdsState;

    pdsState = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);

    if (pdsState.getFirstValue("//security_external") == null) {
      result = aContext.source("session:/currentUser");
    } else {
      INKFRequest request = util.createSourceRequest((String) pdsState.getFirstValue("//security_userIdResource"), null);
      request.setRequestScope(buildRequestScope(aContext, (String) pdsState.getFirstValue("//security_gatekeeperModule")));

      result = aContext.issueRequest(request);
    }
    aContext.createResponseFrom(result);
  }

  @Override
  public void onSink(INKFRequestContext aContext, AccessorUtil util) throws Exception {
    IHDSNode pdsState;

    pdsState = aContext.source("fpds:/nk4um/config.xml", IHDSNode.class);

    if (pdsState.getFirstValue("//security_external") == null) {
      aContext.sink("session:/currentUser", aContext.sourcePrimary(Object.class));
    } else {
      throw new Exception("Internal error: cannot sync to external user resource.");
    }
  }

  private IRequestScopeLevel buildRequestScope(INKFRequestContext aContext, String moduleUri) throws NKFException {
    IIdentifier uri = new SimpleIdentifierImpl(moduleUri);
    Version version = null;
    ISpace space = aContext.getKernelContext().getKernel().getSpace(uri, version, version);
    return RequestScopeLevelImpl.appendScopeLevel(RequestScopeLevelImpl.createOrphanedRootScopeLevel(space, aContext.getKernelContext().getRequestScope()), aContext.getKernelContext().getRequestScope());
  }
}
