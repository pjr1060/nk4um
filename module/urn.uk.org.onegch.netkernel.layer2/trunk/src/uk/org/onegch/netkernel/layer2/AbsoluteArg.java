package uk.org.onegch.netkernel.layer2;

public class AbsoluteArg extends IArg
{
  private String argValue;

  public AbsoluteArg(String argName, String argValue) {
    super(argName);
    this.argValue= argValue;
  }

  public String getArgValue() {
    return argValue;
  }

  public void setArgValue(String argValue) {
    this.argValue= argValue;
  }
  
}
