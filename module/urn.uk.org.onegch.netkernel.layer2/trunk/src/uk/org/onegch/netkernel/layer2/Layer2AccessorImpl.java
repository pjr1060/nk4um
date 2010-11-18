package uk.org.onegch.netkernel.layer2;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

public abstract class Layer2AccessorImpl extends StandardAccessorImpl
{
  public void onSource(INKFRequestContext aContext, AccessorUtil util) throws Exception {}
  public void onSink(INKFRequestContext aContext, AccessorUtil util) throws Exception {}
  public void onExists(INKFRequestContext aContext, AccessorUtil util) throws Exception {}
  public void onDelete(INKFRequestContext aContext, AccessorUtil util) throws Exception {}
  public void onNew(INKFRequestContext aContext, AccessorUtil util) throws Exception {}
  
  @Override
  public final void onSource(INKFRequestContext aContext) throws Exception {
    onSource(aContext, new AccessorUtil(aContext));
  }

  @Override
  public final void onSink(INKFRequestContext aContext) throws Exception {
    onSink(aContext, new AccessorUtil(aContext));
  }

  @Override
  public final void onExists(INKFRequestContext aContext) throws Exception {
    onExists(aContext, new AccessorUtil(aContext));
  }

  @Override
  public final void onDelete(INKFRequestContext aContext) throws Exception {
    onDelete(aContext, new AccessorUtil(aContext));
  }

  @Override
  public final void onNew(INKFRequestContext aContext) throws Exception {
    onNew(aContext, new AccessorUtil(aContext));
  }
}
