package uk.org.onegch.netkernel.layer2;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.NKFException;
import org.netkernel.layer0.representation.IHDSNode;

public class HttpUtil extends AccessorUtil
{
  public HttpUtil(INKFRequestContext context) {
    super(context);
  }
  public String escapeString(String toClean) throws NKFException {
    return super.issueSourceRequest("active:sqlEscapeString", String.class, new ArgByValue("operand", toClean));
  }
  
  public IHDSNode escapeHDS(IHDSNode toClean) throws NKFException {
    return super.issueSourceRequest("active:sqlEscapeHDS", IHDSNode.class, new ArgByValue("operand", toClean));
  }
  
  public void attachGoldenThread(String...ids) throws NKFException {
    IArg[] args= new IArg[ids.length];
    for (int i= 0; i < ids.length; i++) {
      args[i]= new Arg("id", ids[i]);
    }
    super.issueSourceRequest("active:attachGoldenThread", IHDSNode.class, args);
  }
  
  
  public void cutGoldenThread(String...ids) throws NKFException {
    IArg[] args= new IArg[ids.length];
    for (int i= 0; i < ids.length; i++) {
      args[i]= new Arg("id", ids[i]);
    }
    super.issueSourceRequest("active:cutGoldenThread", IHDSNode.class, args);
  }
  
}
