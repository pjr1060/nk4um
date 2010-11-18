package uk.org.onegch.netkernel.layer2;

import org.netkernel.layer0.nkf.INKFResponseReadOnly;

public class PrimaryArgFromResponse extends IPrimaryArg
{
  @SuppressWarnings("rawtypes")
  private INKFResponseReadOnly argValue;

  @SuppressWarnings("rawtypes")
  public PrimaryArgFromResponse(INKFResponseReadOnly argValue) {
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
