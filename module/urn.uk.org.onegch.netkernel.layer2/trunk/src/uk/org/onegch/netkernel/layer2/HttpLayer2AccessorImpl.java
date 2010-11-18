package uk.org.onegch.netkernel.layer2;

import org.netkernel.http.transport.HttpAccessorImpl;
import org.netkernel.layer0.nkf.INKFRequestContext;

public class HttpLayer2AccessorImpl extends HttpAccessorImpl {
  public void onGet(INKFRequestContext aContext, HttpUtil util) throws Exception {
    super.onGet(aContext);
  }
  public void onPost(INKFRequestContext aContext, HttpUtil util) throws Exception {
    super.onGet(aContext);
  }
  public void onDelete(INKFRequestContext aContext, HttpUtil util) throws Exception {
    super.onGet(aContext);
  }
  public void onPut(INKFRequestContext aContext, HttpUtil util) throws Exception {
    super.onGet(aContext);
  }
  public void onPatch(INKFRequestContext aContext, HttpUtil util) throws Exception {
    super.onGet(aContext);
  }
  public void onHead(INKFRequestContext aContext, HttpUtil util) throws Exception {
    super.onGet(aContext);
  }

  @Override
  public void onGet(INKFRequestContext aContext) throws Exception {
    onGet(aContext, new HttpUtil(aContext));
  }

  @Override
  public void onPost(INKFRequestContext aContext) throws Exception {
    onPost(aContext, new HttpUtil(aContext));
  }

  @Override
  public void onDelete(INKFRequestContext aContext) throws Exception {
    onDelete(aContext, new HttpUtil(aContext));
  }

  @Override
  public void onPut(INKFRequestContext aContext) throws Exception {
    onPut(aContext, new HttpUtil(aContext));
  }

  @Override
  public void onPatch(INKFRequestContext aContext) throws Exception {
    onPatch(aContext, new HttpUtil(aContext));
  }

  @Override
  public void onHead(INKFRequestContext aContext) throws Exception {
    onHead(aContext, new HttpUtil(aContext));
  }
}
