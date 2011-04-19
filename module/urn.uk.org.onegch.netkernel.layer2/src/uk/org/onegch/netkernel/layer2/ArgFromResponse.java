package uk.org.onegch.netkernel.layer2;

import org.netkernel.layer0.nkf.INKFResponseReadOnly;

public class ArgFromResponse extends IArg
{
  @SuppressWarnings("rawtypes")
  private INKFResponseReadOnly argValue;

  @SuppressWarnings("rawtypes")
  public ArgFromResponse(String argName, INKFResponseReadOnly argValue) {
    super(argName);
    this.argValue= argValue;
  }

  @SuppressWarnings("rawtypes")
  public INKFResponseReadOnly getArgValue() {
    return argValue;
  }

  @SuppressWarnings("rawtypes")
  public void setArgValue(INKFResponseReadOnly argValue) {
    this.argValue= argValue;
  }
}
