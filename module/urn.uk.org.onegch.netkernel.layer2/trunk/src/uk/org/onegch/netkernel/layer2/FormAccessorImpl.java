package uk.org.onegch.netkernel.layer2;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

public class FormAccessorImpl extends StandardAccessorImpl {
  public void onSource(INKFRequestContext aContext, FormUtil util) throws Exception {}
  public void onSink(INKFRequestContext aContext, FormUtil util) throws Exception {}
  public void onExists(INKFRequestContext aContext, FormUtil util) throws Exception {}
  public void onDelete(INKFRequestContext aContext, FormUtil util) throws Exception {}
  public void onNew(INKFRequestContext aContext, FormUtil util) throws Exception {}
  
  @Override
  public final void onSource(INKFRequestContext aContext) throws Exception {
    onSource(aContext, new FormUtil(aContext));
  }

  @Override
  public final void onSink(INKFRequestContext aContext) throws Exception {
    onSink(aContext, new FormUtil(aContext));
  }

  @Override
  public final void onExists(INKFRequestContext aContext) throws Exception {
    onExists(aContext, new FormUtil(aContext));
  }

  @Override
  public final void onDelete(INKFRequestContext aContext) throws Exception {
    onDelete(aContext, new FormUtil(aContext));
  }

  @Override
  public final void onNew(INKFRequestContext aContext) throws Exception {
    onNew(aContext, new FormUtil(aContext));
  }

}
