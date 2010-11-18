package uk.org.onegch.netkernel.layer2;

import org.netkernel.layer0.nkf.INKFRequest;

public class ArgByRequest extends IArg
{
  private INKFRequest argValue;

  public ArgByRequest(String argName, INKFRequest argValue) {
    super(argName);
    this.argValue= argValue;
  }

  public INKFRequest getArgValue() {
    return argValue;
  }

  public void setArgValue(INKFRequest argValue) {
    this.argValue= argValue;
  }
}
