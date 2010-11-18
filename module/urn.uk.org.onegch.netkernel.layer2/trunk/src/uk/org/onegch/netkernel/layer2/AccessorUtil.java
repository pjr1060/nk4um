package uk.org.onegch.netkernel.layer2;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFRequestReadOnly;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.nkf.INKFResponseReadOnly;
import org.netkernel.layer0.nkf.NKFException;

public class AccessorUtil
{
  INKFRequestContext context;
  
  public AccessorUtil(INKFRequestContext context) {
    this.context= context;
  }
  
  public INKFRequestContext getContext() {
    return context;
  }
  
  // SOURCE
  public INKFResponse issueSourceRequestAsResponse(String identifier, IArg... args) throws NKFException {
    return issueSourceRequestAsResponse(identifier, null, args);
  }
  
  public <T> INKFResponse issueSourceRequestAsResponse(String identifier, Class<T> representation, IArg... args) throws NKFException {
    return context.createResponseFrom(issueSourceRequestForResponse(identifier, representation, args));
  }
  
  @SuppressWarnings("rawtypes")
  public INKFResponseReadOnly issueSourceRequestForResponse(String identifier, IArg... args) throws NKFException {
    return issueSourceRequestForResponse(identifier, null, args);
  }
  
  @SuppressWarnings("unchecked")
  public <T> INKFResponseReadOnly<T> issueSourceRequestForResponse(String identifier, Class<T> representation, IArg... args) throws NKFException {
    return context.issueRequestForResponse(createSourceRequest(identifier, representation, args));
  }
  
  @SuppressWarnings("unchecked")
  public <T> T issueSourceRequest(String identifier, Class<T> representation, IArg... args) throws NKFException {
    return (T)context.issueRequest(createSourceRequest(identifier, representation, args));
  }

  @SuppressWarnings("rawtypes")
  public INKFRequest createSourceRequest(String identifier, Class representation, IArg... args) throws NKFException {
    return createRequest(INKFRequestReadOnly.VERB_SOURCE, identifier, representation, null, args);
  }
  
  // SINK
  public Object issueSinkRequest(String identifier, IPrimaryArg primaryArg, IArg... args) throws NKFException {
    return context.issueRequest(createSinkRequest(identifier, primaryArg, args));
  }
  
  public INKFRequest createSinkRequest(String identifier, IPrimaryArg primaryArg, IArg... args) throws NKFException {
    return createRequest(INKFRequestReadOnly.VERB_SINK, identifier, null, primaryArg, args);
  }
  
  // NEW
  public INKFResponse issueNewRequestAsResponse(String identifier, IPrimaryArg primaryArg, IArg... args) throws NKFException {
    return context.createResponseFrom(issueNewRequestForResponse(identifier, primaryArg, args));
  }
  
  @SuppressWarnings("rawtypes")
  public INKFResponseReadOnly issueNewRequestForResponse(String identifier, IPrimaryArg primaryArg, IArg... args) throws NKFException {
    return context.issueRequestForResponse(createNewRequest(identifier, null, primaryArg, args));
  }

  @SuppressWarnings("unchecked")
  public <T> T issueNewRequest(String identifier, Class<T> representation, IPrimaryArg primaryArg, IArg... args) throws NKFException {
    return (T)context.issueRequest(createNewRequest(identifier, representation, primaryArg, args));
  }

  @SuppressWarnings("rawtypes")
  public INKFRequest createNewRequest(String identifier, Class representation, IPrimaryArg primaryArg, IArg... args) throws NKFException {
    return createRequest(INKFRequestReadOnly.VERB_NEW, identifier, representation, primaryArg, args);
  }
  
  // DELETE
  public INKFResponse issueDeleteRequestAsResponse(String identifier, IArg... args) throws NKFException {
    return context.createResponseFrom(issueDeleteRequestForResponse(identifier, args));
  }

  @SuppressWarnings("rawtypes")
  public INKFResponseReadOnly issueDeleteRequestForResponse(String identifier, IArg... args) throws NKFException {
    return context.issueRequestForResponse(createDeleteRequest(identifier, args));
  }
  
  public Object issueDeleteRequest(String identifier, IArg... args) throws NKFException {
    return context.issueRequest(createDeleteRequest(identifier, args));
  }
  
  public INKFRequest createDeleteRequest(String identifier, IArg... args) throws NKFException {
    return createRequest(INKFRequestReadOnly.VERB_DELETE, identifier, null, null, args);
  }
  
  // EXISTS
  public INKFResponse issueExistsRequestAsResponse(String identifier, IArg... args) throws NKFException {
    return context.createResponseFrom(issueExistsRequestForResponse(identifier, args));
  }

  @SuppressWarnings("rawtypes")
  public INKFResponseReadOnly issueExistsRequestForResponse(String identifier, IArg... args) throws NKFException {
    return context.issueRequestForResponse(createExistsRequest(identifier, args));
  }
  
  public boolean issueExistsRequest(String identifier, IArg... args) throws NKFException {
    return (Boolean)context.issueRequest(createExistsRequest(identifier, args));
  }
  
  public INKFRequest createExistsRequest(String identifier, IArg... args) throws NKFException {
    return createRequest(INKFRequestReadOnly.VERB_EXISTS, identifier, null, null, args);
  }

  @SuppressWarnings("rawtypes")
  private INKFRequest createRequest(int verb, String identifier, Class representation, IPrimaryArg primaryArg, IArg... args) throws NKFException {
    INKFRequest request= context.createRequest(identifier);
    
    request.setVerb(verb);
    
    if (representation != null) {
      request.setRepresentationClass(representation);
    }
    
    if (primaryArg != null) {
      if (primaryArg instanceof PrimaryArgByValue) {
        request.addPrimaryArgument(((PrimaryArgByValue)primaryArg).getArgValue());
      } else if (primaryArg instanceof PrimaryArgFromResponse) {
        request.addPrimaryArgument(((PrimaryArgFromResponse)primaryArg).getArgValue());
      } else if (primaryArg instanceof PrimaryArg) {
        request.addPrimaryArgument(context.source(((PrimaryArg)primaryArg).getArgValue()));
      } else {
        throw new NKFException("Unknown primary argument type");
      }
    }
    
    if (args != null) {
      for (int i= 0; i < args.length; i++) {
        if (args[i] instanceof Arg) {
          Arg argument= (Arg)args[i];
          request.addArgument(argument.getArgName(), argument.getArgValue());
        } else if (args[i] instanceof AbsoluteArg) {
          AbsoluteArg argument= (AbsoluteArg)args[i];
          request.addArgumentAsAbsolute(argument.getArgName(), argument.getArgValue());
        } else if (args[i] instanceof ArgByValue) {
          ArgByValue argument= (ArgByValue)args[i];
          request.addArgumentByValue(argument.getArgName(), argument.getArgValue());
        } else if (args[i] instanceof ArgByRequest) {
          ArgByRequest argument= (ArgByRequest)args[i];
          request.addArgumentByRequest(argument.getArgName(), argument.getArgValue());
        } else if (args[i] instanceof ArgFromResponse) {
          ArgFromResponse argument= (ArgFromResponse)args[i];
          request.addArgumentFromResponse(argument.getArgName(), argument.getArgValue());
        } else {
          throw new NKFException("Unknown argument type");
        }
      }
    }
    
    return request;
  }
}
