package uk.org.onegch.netkernel.layer2;

public class Arg extends IArg
{
  private String argValue;

  public Arg(String argName, String argValue) {
    super(argName);
    this.argValue= argValue;
  }

  public String getArgValue() {
    return argValue;
  }

  public void setArgValue(String argumentValue) {
    this.argValue= argumentValue;
  }
  
}
